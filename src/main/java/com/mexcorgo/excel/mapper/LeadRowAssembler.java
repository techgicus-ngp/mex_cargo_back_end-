package com.mexcorgo.excel.mapper;

import com.mexcorgo.datamodel.*;
import com.mexcorgo.excel.util.CellReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

/**
 * Assembles one complete {@link Lead} aggregate from a single Excel data row.
 *
 * Responsibilities:
 *  1. Delegate field-level extraction to {@link ExcelRowMapper}.
 *  2. Wire bidirectional JPA back-references (child → parent).
 *  3. Add the FollowUp to the Lead's list if present.
 *
 * This class is intentionally kept free of any repository or persistence logic.
 */
@Slf4j
@Component
public class LeadRowAssembler {

    /**
     * @param row  the POI data row (row index ≥ DATA_START_ROW)
     * @param user the {@link User} who owns this lead (resolved by the service layer)
     * @return a fully assembled, transient {@link Lead} ready to be persisted
     */
    public Lead assemble(Row row, User user) {

        // 1. Build shell lead (A1 fields)
        Lead lead = ExcelRowMapper.toLeadShell(row, user);

        // 2. Company (A2) – cascade will persist
        Company company = ExcelRowMapper.toCompany(row);
        lead.setCompany(company);

        // 3. Company Employee (A3)
        CompanyEmployee employee = ExcelRowMapper.toCompanyEmployee(row);
        lead.setCompanyEmployee(employee);

        // 4. End User (A4)
        EndUserDetails endUser = ExcelRowMapper.toEndUserDetails(row);
        lead.setEndUserDetails(endUser);

        // 5. Need (A5)
        Need need = ExcelRowMapper.toNeed(row);
        lead.setNeed(need);

        FollowUp companyFollowUp = ExcelRowMapper.toFollowUpFromCompany(row,lead,user);
        addFollowUpInLead(companyFollowUp,lead);

        FollowUp employeeFollowUp = ExcelRowMapper.toFollowUpFromEmployee(row,lead,user);
        addFollowUpInLead(employeeFollowUp,lead);

        FollowUp endUserFollowUp = ExcelRowMapper.toFollowUpFromEndUser(row,lead,user);
        addFollowUpInLead(endUserFollowUp,lead);

        // 6. FollowUp (A6) – only added when a follow-up date is present
        FollowUp followUp = ExcelRowMapper.toFollowUp(row, lead, user);
        if (followUp != null) {
            lead.getFollowUpList().add(followUp);
        }

        return lead;
    }

    private boolean isFollowUpNotNull(FollowUp followUp){
        if (followUp != null) {
            return true;
        }
        return false;
    }

    private void addFollowUpInLead(FollowUp followUp,Lead lead){
        boolean isFollowUpNotNull = isFollowUpNotNull(followUp);
        if (isFollowUpNotNull) lead.getFollowUpList().add(followUp);
    }
}

