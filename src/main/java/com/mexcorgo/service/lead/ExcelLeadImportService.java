package com.mexcorgo.service.lead;

import com.mexcorgo.datamodel.Lead;
import com.mexcorgo.datamodel.User;
import com.mexcorgo.excel.definition.ExcelRowConstants;
import com.mexcorgo.excel.mapper.LeadRowAssembler;
import com.mexcorgo.excel.model.ImportResult;
import com.mexcorgo.excel.util.CellReader;
import com.mexcorgo.excel.validation.ExcelHeaderValidator;
import com.mexcorgo.excel.validation.HeaderValidationResult;
import com.mexcorgo.repository.LeadRepository;
import com.mexcorgo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Orchestrates the full Excel → DB import pipeline.
 *
 * Pipeline (strict order):
 *  1. Open workbook
 *  2. Validate headers  ← gate: abort early if mandatory columns are wrong
 *  3. Parse rows concurrently safe (each row → Lead aggregate)
 *  4. Persist in a single transaction
 *  5. Return {@link ImportResult}
 *
 * Concurrency:
 *  The public entry-point {@link #importAsync} is annotated {@code @Async}
 *  so the HTTP response returns immediately with a job handle.
 *  Other API calls are NOT blocked while the import runs because each
 *  request runs on its own thread from Spring's task executor pool.
 *
 *  The actual DB writes are done inside {@link #persistBatch} which is
 *  {@code @Transactional} and runs on the async thread – fully independent
 *  of any other concurrent transaction.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelLeadImportService {

    private final ExcelHeaderValidator headerValidator;
    private final LeadRowAssembler rowAssembler;
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Async entry-point.  Returns a {@link CompletableFuture} so the controller
     * can respond immediately with HTTP 202 Accepted while the import runs in
     * the background.  Other API requests are NOT blocked.
     *
     * @param file           the uploaded .xlsx file
     * @param assignedUserId the user who owns all imported leads
     */
    @Async("leadImportExecutor")          // bean defined in AsyncConfig
    public CompletableFuture<ImportResult> importAsync(byte[] fileBytes, Long assignedUserId) {
        try {
            ImportResult result = doImport(fileBytes, assignedUserId);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Import failed for user {}: {}", assignedUserId, e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    // ── Orchestrator ──────────────────────────────────────────────────────────

    private ImportResult doImport(byte[] fileBytes, Long assignedUserId) throws IOException {
        User user = resolveUser(assignedUserId);

        try (InputStream is = new ByteArrayInputStream(fileBytes);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            // ── STEP 1: Validate headers BEFORE reading any data ─────────────
            HeaderValidationResult headerResult = headerValidator.validate(sheet);
            if (!headerResult.isValid()) {
                log.error("Aborting import – header validation failed: {}", headerResult.getMismatches());
                throw new ExcelHeaderMismatchException(
                        "Excel headers do not match the expected template",
                        headerResult.getMismatches());
            }

            // ── STEP 2: Parse rows ────────────────────────────────────────────
            return parseAndPersist(sheet, user);
        }
    }

    // ── Row parsing ───────────────────────────────────────────────────────────

    private ImportResult parseAndPersist(Sheet sheet, User user) {
        List<Lead> batch = new ArrayList<>();
        List<ImportResult.RowError> errors = new ArrayList<>();
        int totalRows = 0;

        for (int rowIdx = ExcelRowConstants.DATA_START_ROW;
             rowIdx <= sheet.getLastRowNum(); rowIdx++) {

            Row row = sheet.getRow(rowIdx);
            if (CellReader.isRowBlank(row)) continue;

            totalRows++;
            int excelRowNumber = rowIdx + 1;   // 1-based for human display

            try {
                Lead lead = rowAssembler.assemble(row, user);
                batch.add(lead);
            } catch (Exception e) {
                log.warn("Row {} skipped – {}", excelRowNumber, e.getMessage());
                errors.add(ImportResult.RowError.builder()
                        .rowNumber(excelRowNumber)
                        .reason(e.getMessage())
                        .build());
            }
        }

        // ── STEP 3: Persist in one transaction ────────────────────────────────
        int saved = persistBatch(batch);

        ImportResult result = ImportResult.builder()
                .totalRowsRead(totalRows)
                .successCount(saved)
                .skipCount(errors.size())
                .errors(errors)
                .build();

        log.info("Import complete – total={} saved={} skipped={}",
                totalRows, saved, errors.size());
        return result;
    }

    /**
     * All DB writes happen here inside a single transaction.
     * Runs on the async thread – independent of any other concurrent request.
     */
    @Transactional
    protected int persistBatch(List<Lead> batch) {
        if (batch.isEmpty()) return 0;
        List<Lead> saved = leadRepository.saveAll(batch);
        return saved.size();
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private User resolveUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    // ── Exception ─────────────────────────────────────────────────────────────

    public static class ExcelHeaderMismatchException extends RuntimeException {
        private final List<String> mismatches;

        public ExcelHeaderMismatchException(String message, List<String> mismatches) {
            super(message);
            this.mismatches = mismatches;
        }

        public List<String> getMismatches() {
            return mismatches;
        }
    }
}