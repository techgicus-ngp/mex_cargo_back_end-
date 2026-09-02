package com.mexcorgo.service.lead.endUserDetails;


import com.mexcorgo.datamodel.EndUserDetails;
import com.mexcorgo.repository.EndUserDetailsRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EndUserDetailsService {

    private final EndUserDetailsRepository endUserDetailsRepository;

    public EndUserDetails getEndUser(Long leadId){
        return endUserDetailsRepository.findByLeadLeadId(leadId);
    }

}
