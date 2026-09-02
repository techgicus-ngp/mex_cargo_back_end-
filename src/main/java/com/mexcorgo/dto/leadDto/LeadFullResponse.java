package com.mexcorgo.dto.leadDto;

import com.mexcorgo.dto.FollowUpDto.FollowUpBasicResponse;
import com.mexcorgo.dto.companyDto.CompanyResponse;
import com.mexcorgo.dto.companyEmployeeDto.CompanyEmployeeResponse;
import com.mexcorgo.dto.endUserDetailsDto.EndUserDetailsResponse;
import com.mexcorgo.dto.needDto.NeedResponse;
import lombok.Data;

import java.util.List;

@Data
public class LeadFullResponse {
    private LeadResponse lead;
    private CompanyResponse company;
    private CompanyEmployeeResponse companyEmployee;
    private EndUserDetailsResponse endUserDetails;
    private NeedResponse need;
    private List<FollowUpBasicResponse> followUps;
}
