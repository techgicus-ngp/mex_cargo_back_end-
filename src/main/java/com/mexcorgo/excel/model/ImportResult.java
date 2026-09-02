package com.mexcorgo.excel.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Immutable summary returned after an import run.
 * Contains counts plus per-row error details for caller inspection.
 */
@Getter
@Builder
public class ImportResult {

    private final int        totalRowsRead;
    private final int        successCount;
    private final int        skipCount;
    private final List<RowError> errors;

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    // ── inner ─────────────────────────────────────────────────────────────────

    /**
     * Describes a single row that could not be imported.
     */
    @Getter
    @Builder
    public static class RowError {
        /** 1-based row number as seen in Excel. */
        private final int    rowNumber;
        private final String reason;
    }
}