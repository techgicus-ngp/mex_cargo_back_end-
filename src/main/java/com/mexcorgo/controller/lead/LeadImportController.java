package com.mexcorgo.controller.lead;

import com.mexcorgo.excel.model.ImportResult;
import com.mexcorgo.service.lead.ExcelLeadImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST endpoint: POST /api/leads/import
 *
 * Returns HTTP 202 Accepted immediately.
 * The actual import runs asynchronously on the "leadImportExecutor" pool,
 * so all other API calls remain fully responsive during the import.
 *
 * For production, replace the inline CompletableFuture callback with a
 * status-tracking table (jobId → status) so clients can poll for results.
 */
@Slf4j
@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadImportController {

    private final ExcelLeadImportService importService;

    /**
     * Upload an Excel file for async lead import.
     *
     * @param file   multipart field name "file"
     * @param userId the user ID to assign leads to
     */
    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public ResponseEntity<Map<String, Object>> importLeads(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) throws IOException {

        // ── guard: basic file checks before handing off ───────────────────────
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Uploaded file is empty."));
        }

        String filename = file.getOriginalFilename();
        if (filename == null
                || (!filename.toLowerCase().endsWith(".xlsx")
                && !filename.toLowerCase().endsWith(".xls"))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Only .xlsx / .xls files are accepted."));
        }


        byte[] fileBytes = file.getBytes(); // ✅ read before async


        // ── fire async import ─────────────────────────────────────────────────
        CompletableFuture<ImportResult> future = importService.importAsync(fileBytes, userId);

        // Log result when done (non-blocking)
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Async import failed for userId={}: {}", userId, ex.getMessage());
            } else {
                log.info("Async import finished for userId={}: saved={} skipped={}",
                        userId, result.getSuccessCount(), result.getSkipCount());
            }
        });

        // Return 202 immediately – the client does not wait for rows to be saved
        return ResponseEntity.accepted()
                .body(Map.of(
                        "message", "Import started. Check server logs or a status endpoint for results.",
                        "file",    filename,
                        "userId",  userId
                ));
    }
}
