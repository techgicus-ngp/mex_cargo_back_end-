package com.mexcorgo.excel.mapper;

import com.mexcorgo.component.*;
import com.mexcorgo.datamodel.*;
import com.mexcorgo.excel.definition.ExcelHeaderDefinition;
import com.mexcorgo.excel.util.CellReader;
import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Row;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.mexcorgo.excel.definition.ExcelHeaderDefinition.*;

/**
 * Pure mapping functions: Excel {@link Row} → domain entity.
 *
 * Rules:
 *  - One static method per entity.
 *  - No repository calls, no Spring beans – pure transformation only.
 *  - Lead ↔ child back-references are wired by {@link LeadRowAssembler}.
 */
@UtilityClass
public class ExcelRowMapper {

    // ── Company (A2) ─────────────────────────────────────────────────────────

    public Company toCompany(Row row) {
        return Company.builder()
                .companyName(CellReader.asString(row, CO_NAME))
                .companySector(CellReader.asString(row, CO_SECTOR))
                .companySetUp(CellReader.asString(row, CO_SETUP))
                .state(CellReader.asString(row, CO_STATE))
                .location(CellReader.asString(row, CO_LOCATION))
                .minorHub(CellReader.asString(row, CO_MINOR_HUB))
                .majorHub(CellReader.asString(row, CO_MAJOR_HUB))
                .build();
    }

    // ── CompanyEmployee (A3) ──────────────────────────────────────────────────

    public CompanyEmployee toCompanyEmployee(Row row) {
        return CompanyEmployee.builder()
                .employeeName(CellReader.asString(row, EMP_NAME))
                .department(CellReader.asString(row, EMP_DEPARTMENT))
                .designation(CellReader.asString(row, EMP_DESIGNATION))
                .contactNo(CellReader.asString(row, EMP_MOB))
                .landLineNo(CellReader.asString(row, EMP_LANDLINE))
                .mailId(CellReader.asString(row, EMP_EMAIL))
                .build();
    }

    // ── EndUserDetails (A4) ───────────────────────────────────────────────────

    public EndUserDetails toEndUserDetails(Row row) {
        return EndUserDetails.builder()
                .userName(CellReader.asString(row, EU_NAME))
                .division(null)
                .endUserDepartment(CellReader.asString(row, EU_DEPARTMENT))
                .endUserDepartment(CellReader.asString(row, EU_DESIGNATION))
                .endUserContactNo(CellReader.asString(row, EU_MOB))
                .endUserLandLineNo(CellReader.asString(row, EU_LANDLINE))
                .endUserMailId(CellReader.asString(row, EU_EMAIL))
                .build();
    }

    // ── Need (A5) ─────────────────────────────────────────────────────────────

    public Need toNeed(Row row) {
        return Need.builder()
                .source(CellReader.asString(row, NEED_ORIGIN))
                .originFloorNo(CellReader.asString(row, NEED_ORIGIN_FLOOR))
                .isLiftAvailableInOrigin(CellReader.asBoolean(row, NEED_LIFT_ORIGIN))
                .specialService(CellReader.asString(row, NEED_LOADING_DIST_1))
                .secondaryVehicle(CellReader.asString(row, NEED_LOADING_DIST_2))
                // Destination block – add columns when template is extended
                .build();
    }

    // ── FollowUp (A6) ─────────────────────────────────────────────────────────

    /**
     * Returns {@code null} when there is no follow-up date in the row,
     * so the assembler can skip adding it to the list.
     */
    public FollowUp toFollowUp(Row row, Lead lead, User user) {
        LocalDate fuDate = CellReader.asLocalDate(row, FU_DATE);
        if (fuDate == null) return null;

        return FollowUp.builder()
                .followUpDate(fuDate)
                .followUpTime(CellReader.asLocalTime(row, FU_TIME))
                .followUpStatus(FollowUpStatus.ONGOING)
                .lead(lead)
                .user(user)
                .build();
    }

    // ── FollowUp from A2 (Company future date) ────────────────────────────────

    public FollowUp toFollowUpFromCompany(Row row, Lead lead, User user) {
        LocalDate fuDate = CellReader.asLocalDate(row, CO_FUTURE_DATE);
        if (fuDate == null) return null;

        return FollowUp.builder()
                .followUpDate(fuDate)
                .followUpTime(CellReader.asLocalTime(row, CO_FUTURE_TIME))
                .remark(CellReader.asString(row, CO_FUTURE_REMARK))
                .followUpStatus(FollowUpStatus.ONGOING)
                .lead(lead)
                .user(user)
                .build();
    }

    // ── FollowUp from A3 (Employee future date) ───────────────────────────────

    public FollowUp toFollowUpFromEmployee(Row row, Lead lead, User user) {
        LocalDate fuDate = CellReader.asLocalDate(row, EMP_FUTURE_DATE);
        if (fuDate == null) return null;

        return FollowUp.builder()
                .followUpDate(fuDate)
                .followUpTime(CellReader.asLocalTime(row, EMP_FUTURE_TIME))
                .remark(CellReader.asString(row, EMP_FUTURE_REMARK))
                .followUpStatus(FollowUpStatus.ONGOING)
                .lead(lead)
                .user(user)
                .build();
    }

// ── FollowUp from A4 (End User future date) ───────────────────────────────

    public FollowUp toFollowUpFromEndUser(Row row, Lead lead, User user) {
        LocalDate fuDate = CellReader.asLocalDate(row, EU_FUTURE_DATE);
        if (fuDate == null) return null;

        return FollowUp.builder()
                .followUpDate(fuDate)
                .followUpTime(CellReader.asLocalTime(row, EU_FUTURE_TIME))
                .remark(CellReader.asString(row, EU_FUTURE_REMARK_1))
                .followUpStatus(FollowUpStatus.ONGOING)
                .lead(lead)
                .user(user)
                .build();
    }



    // ── Lead core (A1) ────────────────────────────────────────────────────────

    public Lead toLeadShell(Row row, User user) {
        return Lead.builder()
                .leadReferenceNo(CellReader.asString(row, LEAD_SNO))
                .leadDate(CellReader.asLocalDate(row, LEAD_DATE))
                .leadTime(CellReader.asLocalTime(row, LEAD_TIME))
                .modeOfCommunication(CellReader.asEnum(row, LEAD_MODE_OF_COMM, ModeOfCommunication.class))
                .wayOfLead(CellReader.asEnum(row, LEAD_SOURCE, WayOfLead.class))
                .dataReference(parseDataReference(CellReader.asString(row, LEAD_REFERENCE_DETAIL)))
                .dataReferenceNo(CellReader.asString(row, LEAD_REFERENCE_NO))
                .currentStatus(LeadStatus.CREATED)
                .followUpStatus(FollowUpStatus.ONGOING)
                .isQuatationCreated(false)
                .isQuatationSendToUser(false)
                .leadCreatedDate(LocalDate.now())
                .leadCreatedTime(LocalTime.now())
                .user(user)
                .build();
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private DataReference parseDataReference(String raw) {
        if (raw == null) return null;
        String u = raw.toUpperCase();
        if (u.startsWith("A")) return DataReference.LEAD_PROSECUTOR;
        if (u.startsWith("B")) return DataReference.CORPORATE_REFERENCE;
        if (u.startsWith("C")) return DataReference.BULK_DIRECTLY;
        if (u.startsWith("D")) return DataReference.LEAD_REFERENCE_PROSECUTOR;
        if (u.startsWith("E")) return DataReference.BULK_TRANSFER_LIST;
        if (u.startsWith("F")) return DataReference.GMB;
        if (u.startsWith("G")) return DataReference.LEAD_PURCHASE;
        if (u.startsWith("H")) return DataReference.CAMPAIGNING;
        if (u.startsWith("I")) return DataReference.SOCIAL_MEDIA_MARKETING;
        if (u.startsWith("J")) return DataReference.ASSOCIATE_PARTNER;
        if (u.startsWith("K")) return DataReference.SALES_TEAM_REFERRAL;
        return DataReference.OTHER;
    }
}
