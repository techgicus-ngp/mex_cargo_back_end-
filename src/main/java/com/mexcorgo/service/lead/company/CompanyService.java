package com.mexcorgo.service.lead.company;


import com.mexcorgo.datamodel.Company;
import com.mexcorgo.dto.companyDto.CompanyRequest;
import com.mexcorgo.mapper.CompanyMapper;
import com.mexcorgo.repository.CompanyRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public Company getCompany(Long leadId){
        return companyRepository.findByLead_LeadId(leadId);
    }

    public Company updateCompany(Long leadId, CompanyRequest companyRequest){
        Company existingCompany = companyRepository.findByLead_LeadId(leadId);
        companyMapper.updateEntity(companyRequest,existingCompany);

        return companyRepository.save(existingCompany);
    }

}
