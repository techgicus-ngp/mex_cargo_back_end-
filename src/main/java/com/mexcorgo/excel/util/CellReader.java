package com.mexcorgo.excel.util;

import com.mexcorgo.excel.definition.ExcelHeaderDefinition;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Stateless utility that reads a single {@link Cell} and converts it to the
 * requested Java type.
 *
 * All public methods accept both an explicit {@code colIndex} and the
 * convenience overload that takes an {@link ExcelHeaderDefinition} enum constant
 * so call-sites stay readable without scattering magic numbers.
 */
@Slf4j
@UtilityClass
public class CellReader {

    // ── String ────────────────────────────────────────────────────────────────

    public String asString(Row row, ExcelHeaderDefinition col) {
        return asString(row, col.getColIndex());
    }

    public String asString(Row row, int colIndex) {
        Cell cell = getCell(row, colIndex);
        if (cell == null) return null;

        String value = switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue().toLocalDate().toString()
                    : formatNumeric(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> resolveFormula(cell);
            default      -> null;
        };

        return (value != null && !value.isBlank()) ? value.trim() : null;
    }

    // ── LocalDate ─────────────────────────────────────────────────────────────

    public LocalDate asLocalDate(Row row, ExcelHeaderDefinition col) {
        return asLocalDate(row, col.getColIndex());
    }

    public LocalDate asLocalDate(Row row, int colIndex) {
        Cell cell = getCell(row, colIndex);
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            }
            // Fallback: try parsing the string representation
            String raw = asString(row, colIndex);
            return raw != null ? LocalDate.parse(raw) : null;
        } catch (Exception e) {
            log.debug("Cannot parse LocalDate at col {}: {}", colIndex, e.getMessage());
            return null;
        }
    }

    // ── LocalTime ─────────────────────────────────────────────────────────────

    public LocalTime asLocalTime(Row row, ExcelHeaderDefinition col) {
        return asLocalTime(row, col.getColIndex());
    }

    public LocalTime asLocalTime(Row row, int colIndex) {
        Cell cell = getCell(row, colIndex);
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalTime();
            }
            String raw = asString(row, colIndex);
            return raw != null ? LocalTime.parse(raw) : null;
        } catch (Exception e) {
            log.debug("Cannot parse LocalTime at col {}: {}", colIndex, e.getMessage());
            return null;
        }
    }

    // ── Boolean ───────────────────────────────────────────────────────────────

    public Boolean asBoolean(Row row, ExcelHeaderDefinition col) {
        return asBoolean(row, col.getColIndex());
    }

    public Boolean asBoolean(Row row, int colIndex) {
        String raw = asString(row, colIndex);
        if (raw == null) return null;
        return raw.equalsIgnoreCase("YES") || raw.equalsIgnoreCase("TRUE") || raw.equals("1");
    }

    // ── Enum ──────────────────────────────────────────────────────────────────

    public <E extends Enum<E>> E asEnum(Row row, ExcelHeaderDefinition col, Class<E> type) {
        return asEnum(row, col.getColIndex(), type);
    }

    public <E extends Enum<E>> E asEnum(Row row, int colIndex, Class<E> type) {
        String raw = asString(row, colIndex);
        if (raw == null) return null;

        // Try exact match first
        try {
            return Enum.valueOf(type, raw.toUpperCase().replace(" ", "_").replace("-", "_"));
        } catch (IllegalArgumentException ex) {
            // Fuzzy: find constant whose name is contained in the cell value
            for (E constant : type.getEnumConstants()) {
                if (raw.toUpperCase().contains(constant.name().replace("_", " "))) {
                    return constant;
                }
            }
            log.debug("No {} constant matched '{}' at col {}", type.getSimpleName(), raw, colIndex);
            return null;
        }
    }

    // ── Row-empty check ───────────────────────────────────────────────────────

    public boolean isRowBlank(Row row) {
        if (row == null) return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String val = asString(row, c);
                if (val != null && !val.isBlank()) return false;
            }
        }
        return true;
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private Cell getCell(Row row, int colIndex) {
        if (row == null) return null;
        return row.getCell(colIndex);
    }

    private String formatNumeric(double d) {
        // Avoid "1.0" for whole numbers stored as double
        return (d == Math.floor(d) && !Double.isInfinite(d))
                ? String.valueOf((long) d)
                : String.valueOf(d);
    }

    private String resolveFormula(Cell cell) {
        try {
            return switch (cell.getCachedFormulaResultType()) {
                case STRING  -> cell.getRichStringCellValue().getString();
                case NUMERIC -> formatNumeric(cell.getNumericCellValue());
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                default      -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }
}
