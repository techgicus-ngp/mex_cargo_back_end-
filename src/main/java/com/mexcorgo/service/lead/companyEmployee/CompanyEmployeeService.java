package com.mexcorgo.service.lead.companyEmployee;


import com.mexcorgo.datamodel.CompanyEmployee;
import com.mexcorgo.dto.companyEmployeeDto.CompanyEmployeeRequest;
import com.mexcorgo.mapper.CompanyEmployeeMapper;
import com.mexcorgo.repository.CompanyEmployeeRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CompanyEmployeeService {


    private final CompanyEmployeeMapper companyEmployeeMapper;
    private final CompanyEmployeeRepository companyEmployeeRepository;

    public CompanyEmployee getCompanyEmployee(Long leadId){
        return companyEmployeeRepository.findByLead_LeadId(leadId);
    }


    public CompanyEmployee updateCompanyEmployee(Long leadId, CompanyEmployeeRequest request){
        CompanyEmployee existingCompanyEmployee = companyEmployeeRepository.findByLead_LeadId(leadId);
        companyEmployeeMapper.updateEntity(request,existingCompanyEmployee);

        return companyEmployeeRepository.save(existingCompanyEmployee);
    }
}
