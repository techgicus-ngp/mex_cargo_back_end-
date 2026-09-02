package com.mexcorgo.excel.validation;

import com.mexcorgo.excel.definition.ExcelHeaderDefinition;
import com.mexcorgo.excel.definition.ExcelRowConstants;
import com.mexcorgo.excel.util.CellReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * Validates that every column defined in {@link ExcelHeaderDefinition}
 * exists at the expected position with the expected header text.
 *
 * Strategy:
 *  - ALL columns are checked and reported together (fail-fast-all, not fail-fast-first).
 *  - Only columns flagged as {@link ExcelHeaderDefinition#isMandatory()} cause the
 *    overall result to be INVALID.  Non-mandatory mismatches are logged as warnings
 *    so the caller can still decide to abort if desired.
 */
@Slf4j
@Component
public class ExcelHeaderValidator {

    /**
     * @param sheet the first sheet of the uploaded workbook
     * @return validation result carrying every mismatch found
     */
    public HeaderValidationResult validate(Sheet sheet) {
        Row headerRow = sheet.getRow(ExcelRowConstants.HEADER_ROW);

        if (headerRow == null) {
            return HeaderValidationResult.fail(
                    List.of("Header row (row " + (ExcelRowConstants.HEADER_ROW + 1)
                            + ") is completely missing from the sheet."));
        }

        List<String> mismatches = new ArrayList<>();
        boolean mandatoryFailed = false;

        for (ExcelHeaderDefinition col : ExcelHeaderDefinition.values()) {
            String actual   = CellReader.asString(headerRow, col.getColIndex());
            String expected = col.getExpectedHeader();

            boolean matches = normalise(actual).equals(normalise(expected));

            if (!matches) {
                String msg = String.format(
                        "[%s | col %d] expected='%s'  actual='%s'",
                        col.getSectionCode(), col.getColIndex(), expected, actual == null ? "<empty>" : actual);

                if (col.isMandatory()) {
                    log.error("MANDATORY header mismatch → {}", msg);
                    mandatoryFailed = true;
                } else {
                    log.warn("Optional header mismatch → {}", msg);
                }
                mismatches.add(msg);
            }
        }

        if (mandatoryFailed) {
            log.error("Header validation FAILED – {} mismatch(es) found.", mismatches.size());
            return HeaderValidationResult.fail(mismatches);
        }

        if (!mismatches.isEmpty()) {
            log.warn("Header validation passed with {} non-mandatory warning(s).", mismatches.size());
        } else {
            log.info("Header validation passed – all {} columns matched.", ExcelHeaderDefinition.values().length);
        }

        return HeaderValidationResult.ok();
    }

    // ── private ───────────────────────────────────────────────────────────────

    /**
     * Strips leading/trailing whitespace, collapses inner whitespace to a
     * single space, and upper-cases – so minor formatting differences in the
     * template do not trigger false failures.
     */
    private String normalise(String raw) {
        if (raw == null) return "";
        return raw.trim().replaceAll("\\s+", " ").toUpperCase();
    }
}

