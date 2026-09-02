package com.mexcorgo.excel.validation;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Immutable result of a header-validation pass.
 * Call {@link #isValid()} before proceeding with row parsing.
 */
@Getter
@Builder
public class HeaderValidationResult {

    private final boolean      valid;
    private final List<String> mismatches;   // describes every column that failed

    public boolean isValid() {
        return valid;
    }

    /** Convenience factory – all OK. */
    public static HeaderValidationResult ok() {
        return HeaderValidationResult.builder()
                .valid(true)
                .mismatches(List.of())
                .build();
    }

    /** Convenience factory – at least one mismatch. */
    public static HeaderValidationResult fail(List<String> mismatches) {
        return HeaderValidationResult.builder()
                .valid(false)
                .mismatches(mismatches)
                .build();
    }

    @Override
    public String toString() {
        return valid
                ? "HeaderValidationResult[VALID]"
                : "HeaderValidationResult[INVALID] mismatches=" + mismatches;
    }
}
