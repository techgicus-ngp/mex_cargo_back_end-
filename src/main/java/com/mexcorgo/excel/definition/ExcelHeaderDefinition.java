package com.mexcorgo.excel.definition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;



@Getter
@RequiredArgsConstructor
public enum ExcelHeaderDefinition {

    // ── A1 – Lead core ────────────────────────────────────────────────────────
    LEAD_SNO              (0,  "A1.1", "S.NO."),
    LEAD_DATE             (1,  "A1.2", "DATE"),
    LEAD_TIME             (2,  "A1.3", "TIME"),
    LEAD_MODE_OF_COMM     (3,  "A1.4", "MODE OF COMMUNICATION"),
    LEAD_SOURCE           (4,  "A1.5", "SOURCE"),
    LEAD_REFERENCE_DETAIL (5,  "A1.6", "REFERENCE DETA"),
    LEAD_REFERENCE_NO     (6,  "A1.7", "REFERENCE NO"),
    LEAD_SECTOR           (7,  "A1.8", "SECTOR"),
    LEAD_SUB_SECTOR       (8,  "A1.9", "SUB SECTOR"),

    // ── A2 – Company ──────────────────────────────────────────────────────────
    CO_NAME               (9,  "A2.1",  "COMAPNY"),          // intentional typo in template
    CO_SECTOR             (10, "A2.2",  "SECTOR"),
    CO_SUB_SECTOR         (11, "A2.3",  "SUB SECTOR"),
    CO_PRODUCT            (12, "A2.4",  "PRODUCT"),
    CO_VENDOR_CODE        (13, "A2.5",  "VENDOR CODE"),
    CO_VENDOR_CODING_DATE (14, "A2.6",  "VENDOR CODING DATE"),
    CO_CATEGORY           (15, "A2.7",  "CATEGORY A ------F"),
    CO_REMARK             (16, "A2.8",  "REMARK 1 CE\\CP\\CF\\CA"),
    CO_FACTORIES          (17, "A2.9",  "FACTORIES"),
    CO_OFFICES            (18, "A2.10", "OFFICES"),
    CO_OFFICE_1           (19, "A2.11", "OFFICE 1"),
    CO_OFFICE_2           (20, "A2.12", "OFFICE 2"),
    CO_OFFICE_3           (21, "A2.13", "OFFICE3"),
    CO_MONTHLY_VOLUME     (22, "A2.14", "MONTHLY VOLUME( NOT MANDATORY)"),
    CO_YEARLY_VOLUME      (23, "A2.15", "YEARLY VOLUME( NOT MANDATORY)"),
    CO_SETUP              (24, "A2.16", "SETUP"),
    CO_LOCATION           (25, "A2.17", "LOCATION"),
    CO_PINCODE            (26, "A2.18", "PINCODE"),
    CO_LANDMARK           (27, "A2.19", "LAND MARK"),
    CO_ADD1               (28, "A2.20", "ADD1"),
    CO_ADD2               (29, "A2.21", "ADD2"),
    CO_ADD3               (30, "A2.22", "ADD3"),
    CO_STATE              (31, "A2.23", "STATE"),
    CO_MINOR_HUB          (32, "A2.24", "MINOR HUB"),
    CO_DIST_MINOR_HUB     (33, "A2.25", "DISTANCE FROM MINOR HUB"),
    CO_MAJOR_HUB          (34, "A2.26", "MAJOR HUB"),
    CO_DIST_MAJOR_HUB     (35, "A2.27", "DISTANCE FROM MAJOR HUB"),
    CO_WEBSITE            (36, "A2.28", "WEBSITE"),
    CO_EMAIL              (37, "A2.29", "EMAIL"),
    CO_PHONE_1            (38, "A2.30", "PHONE 1"),
    CO_PHONE_2            (39, "A2.31", "PHONE2"),
    CO_PHONE_3            (40, "A2.32", "PHONE3"),
    CO_CONTACT_DATE       (41, "A2.33", "DATE"),
    CO_MODE_OF_CONTACT    (42, "A2.34", "MODE OF CONTACT"),
    CO_IN_OUT_BOND        (43, "A2.35", "IN bond \\out bond"),
    CO_APPLICABLE_CONTACT (44, "A2.36", "APPLICABLE CONTACT - REAL TIME PRINT HOGA"),
    CO_TIME_IN            (45, "A2.37", "TIME IN"),
    CO_TIME_OUT           (46, "A2.38", "TIME OUT"),
    CO_DURATION           (47, "A2.39", "DURATION"),
    CO_REMARK_2           (48, "A2.40", "REMARK  2"),
    CO_GRADE              (49, "A2.41", "GRADE"),
    CO_NOTE               (50, "A2.42", "NOTE"),
    CO_FUTURE_DATE        (51, "A2.43", "FUTURE KA DATE"),
    CO_FUTURE_TIME        (52, "A2.44", "FUTURE TIME"),
    CO_FUTURE_REMARK      (53, "A2.45", "FUTURE REMARK"),

    // ── A3 – Company Employee ─────────────────────────────────────────────────
    EMP_NAME              (54, "A3.1",  "EMPLOYEE NAME"),
    EMP_DIVISION          (55, "A3.2",  "DIVISION"),
    EMP_DEPARTMENT        (56, "A3.3",  "DEPARTMENT"),
    EMP_DESIGNATION       (57, "A3.4",  "DESIGNATION"),
    EMP_MOB               (58, "A3.5",  "MOB NO"),
    EMP_LANDLINE          (59, "A3.6",  "LAND LINE"),
    EMP_EMAIL             (60, "A3.7",  "EMAIL"),
    EMP_MODE_OF_CONTACT   (61, "A3.8",  "MODE OF CONTACT"),
    EMP_IN_OUT_BOND       (62, "A3.9",  "IN bond \\out bond"),
    EMP_APPLICABLE        (63, "A3.10", "APPLICABLE CONTACT - REAL TIME PRINT HOGA"),
    EMP_TIME_IN           (64, "A3.11", "TIME IN"),
    EMP_TIME_OUT          (65, "A3.12", "TIME OUT"),
    EMP_DURATION          (66, "A3.13", "DURATION"),
    EMP_REMARK            (67, "A3.14", "REMARK  3"),
    EMP_GRADE             (68, "A3.15", "GRADE"),
    EMP_NOTE              (69, "A3.16", "NOTE"),
    EMP_FUTURE_DATE       (70, "A3.17", "FUTURE KA DATE"),
    EMP_FUTURE_TIME       (71, "A3.18", "FUTURE TIME"),
    EMP_FUTURE_REMARK     (72, "A3.19", "FUTURE REMARK"),

    // ── A4 – End User ─────────────────────────────────────────────────────────
    EU_NAME               (73, "A4.1",  "END USER NAME"),
    EU_DIVISION           (74, "A4.2",  "DIVISION"),
    EU_DEPARTMENT         (75, "A4.3",  "DEPARTMENT"),
    EU_DESIGNATION        (76, "A4.4",  "DESIGNATION"),
    EU_MOB                (77, "A4.5",  "MOB NO"),
    EU_LANDLINE           (78, "A4.6",  "LAND LINE"),
    EU_EMAIL              (79, "A4.7",  "EMAIL"),
    EU_MODE_OF_CONTACT    (80, "A4.8",  "MODE OF CONTACT"),
    EU_IN_OUT_BOND        (81, "A4.9",  "IN bond \\out bond"),
    EU_APPLICABLE         (82, "A4.10", "APPLICABLE CONTACT - REAL TIME PRINT HOGA"),
    EU_TIME_IN            (83, "A4.11", "TIME IN"),
    EU_TIME_OUT           (84, "A4.12", "TIME OUT"),
    EU_DURATION           (85, "A4.13", "DURATION"),
    EU_REMARK             (86, "A4.14", "REMARK  3"),
    EU_GRADE              (87, "A4.15", "GRADE"),
    EU_NOTE               (88, "A4.16", "NOTE"),
    EU_FUTURE_DATE        (89, "A4.17", "FUTURE KA DATE"),
    EU_FUTURE_TIME        (90, "A4.18", "FUTURE TIME"),
    EU_FUTURE_REMARK_1    (91, "A4.19", "FUTURE REMARK"),
    EU_FUTURE_REMARK_2    (92, "A4.20", "FUTURE REMARK"),

    // ── A5 – Need / Origin ────────────────────────────────────────────────────
    NEED_ORIGIN           (93,  "A5.1",  "ORIGIN"),
    NEED_ORIGIN_PINCODE   (94,  "A5.2",  "PINCODE"),
    NEED_ORIGIN_MINOR_HUB (95,  "A5.3",  "MINOR HUB"),
    NEED_ORIGIN_DIST_MIN  (96,  "A5.4",  "DIS."),
    NEED_ORIGIN_MAJOR_HUB (97,  "A5.5",  "MAJOR HUB"),
    NEED_ORIGIN_DIST_MAJ  (98,  "A5.6",  "DIS."),
    NEED_ORIGIN_STATE     (99,  "A5.7",  "STATE"),
    NEED_ORIGIN_LANDMARK  (100, "A5.8",  "LANDMARK"),
    NEED_ORIGIN_ADD1      (101, "A5.9",  "ADD1"),
    NEED_ORIGIN_ADD2      (102, "A5.10", "ADD2"),
    NEED_ORIGIN_ADD3      (103, "A5.11", "ADD3"),
    NEED_ORIGIN_NO_ENTRY  (104, "A5.12", "NO ENTRY"),
    NEED_LOADING_DIST_1   (105, "A5.13", "LOADING DISTANCE 1( DIRECT VEHICHLE )"),
    NEED_LOADING_DIST_2   (106, "A5.14", "LOADING  DISTANCE 2( SECONDARY VEHICLE )"),
    NEED_ORIGIN_FLOOR     (107, "A5.15", "FLOOR NO"),
    NEED_LIFT_ORIGIN      (108, "A5.16", "LIFT AVAILABLE OR NOT"),

    // ── A6 – Follow-Up ────────────────────────────────────────────────────────
    FU_DATE               (158, "A6.1", "FOLLOW UP DATE"),
    FU_TIME               (159, "A6.2", "TIME");

    // ─────────────────────────────────────────────────────────────────────────

    private final int    colIndex;
    private final String sectionCode;
    private final String expectedHeader;

    /** Mandatory columns – validation fails if ANY of these headers mismatch. */
    public boolean isMandatory() {
        return switch (this) {
            case LEAD_DATE, LEAD_MODE_OF_COMM, LEAD_SOURCE,
                 CO_NAME, EMP_NAME, EU_NAME -> true;
            default -> false;
        };
    }
}