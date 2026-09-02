package com.mexcorgo.service.lead.need;


import com.mexcorgo.datamodel.AdditionalNeed;
import com.mexcorgo.datamodel.Need;
import com.mexcorgo.dto.needDto.NeedRequest;
import com.mexcorgo.mapper.NeedMapper;
import com.mexcorgo.repository.NeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NeedService {

    private final NeedRepository needRepository;
    private final NeedMapper needMapper;

    public Need getNeed(Long leadId){
        return needRepository.findByLead_LeadId(leadId);
    }

    public Need updateNeed(Long leadId, NeedRequest needRequest){
        Need existingNeed = needRepository.findByLead_LeadId(leadId);
        needMapper.updateEntity(needRequest,existingNeed);
    return needRepository.save(existingNeed);
    }
}
