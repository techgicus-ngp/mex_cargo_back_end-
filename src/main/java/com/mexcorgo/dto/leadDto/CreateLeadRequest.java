package com.mexcorgo.dto.leadDto;

import com.mexcorgo.dto.FollowUpDto.FollowUpRequest;
import com.mexcorgo.dto.companyDto.CompanyRequest;
import com.mexcorgo.dto.companyEmployeeDto.CompanyEmployeeRequest;
import com.mexcorgo.dto.endUserDetailsDto.EndUserDetailsRequest;
import com.mexcorgo.dto.needDto.NeedRequest;
import com.mexcorgo.dto.request.AdditionalNeedRequest;
import lombok.Data;

import java.util.List;

@Data
public class CreateLeadRequest {
    private LeadRequest lead;
    private CompanyRequest company;
    private CompanyEmployeeRequest companyEmployee;
    private EndUserDetailsRequest endUserDetails;
    private NeedRequest need;
    private FollowUpRequest followUp;
}
