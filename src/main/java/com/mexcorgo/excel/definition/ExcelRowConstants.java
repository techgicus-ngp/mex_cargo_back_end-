package com.mexcorgo.excel.definition;



/**
 * Row-index constants for the Lead import Excel template (0-based).
 * Change here only if the template layout shifts.
 */
public final class ExcelRowConstants {

    private ExcelRowConstants() {}

    /** Row that carries section codes like A1.1, A2.3 … */
    public static final int SECTION_CODE_ROW = 1;

    /** Row that carries human-readable column headers. */
    public static final int HEADER_ROW = 2;

    /** First row that contains actual data. */
    public static final int DATA_START_ROW = 3;
}
