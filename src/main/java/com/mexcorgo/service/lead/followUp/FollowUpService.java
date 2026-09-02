package com.mexcorgo.service.lead.followUp;


import com.mexcorgo.component.FollowUpStatus;
import com.mexcorgo.datamodel.EndUserDetails;
import com.mexcorgo.datamodel.FollowUp;
import com.mexcorgo.datamodel.Lead;
import com.mexcorgo.datamodel.Need;
import com.mexcorgo.dto.FollowUpDto.FollowUpBasicResponse;
import com.mexcorgo.dto.FollowUpDto.FollowUpRequest;
import com.mexcorgo.dto.FollowUpDto.FollowUpSummaryResponse;
import com.mexcorgo.exception.LeadAlreadyCompletedException;
import com.mexcorgo.exception.ResourceNotFoundException;
import com.mexcorgo.mapper.FollowUpMapper;
import com.mexcorgo.repository.FollowUpRepository;
import com.mexcorgo.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowUpService {


    private final LeadRepository leadRepository;
    private final FollowUpRepository followUpRepository;
    private final FollowUpMapper followUpMapper;


    @Transactional
    public FollowUpBasicResponse addFollowUp(Long leadId, FollowUpRequest followUpRequest){

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(()-> new ResourceNotFoundException("Lead not found with id : "+leadId));

        if (lead.getFollowUpStatus().equals(FollowUpStatus.COMPLETED)){
            throw new LeadAlreadyCompletedException("Lead already completed, so you can not add the followUp.");
        }

        LocalDate followUpDate = followUpRequest.getFollowUpDate();
        LocalDate nextFolloUpDate = followUpRequest.getNextFollowUpDate();

        if (followUpDate.isBefore(lead.getLeadDate())){
            throw new IllegalArgumentException("FollowUp date is cannot be earlier than the lead date." +
                    "The lead date is "+lead.getLeadDate()+" and followUp date is : "+followUpDate);
        }

        if (followUpRequest.getFollowUpStatus().equals(FollowUpStatus.COMPLETED)){

            // Check: latest follow-up logic
            LocalDate latestFollowUpDate = followUpRepository
                    .findMaxFollowUpDateByLead(lead);

            // If latest date exists and request date is before it → invalid
            if (latestFollowUpDate != null && followUpDate.isBefore(latestFollowUpDate)) {
                throw new IllegalArgumentException("The most recent follow-up happened on "+
                        latestFollowUpDate + ", but you are trying to mark the lead as completed on " +
                        followUpDate + ", which is earlier date.");
            }

            lead.setLeadCompletedDate(followUpDate);

        }

        if (nextFolloUpDate !=null){
            if (nextFolloUpDate.isBefore(followUpDate)){
                throw new IllegalArgumentException("Next follow Up date cannot be earlier than the followUp date." +
                        "Next followUp date : "+nextFolloUpDate+", followUp date : "+followUpDate);
            }
        }

        lead.setFollowUpStatus(followUpRequest.getFollowUpStatus());

        leadRepository.save(lead);

        FollowUp  followUp = followUpMapper.toEntity(followUpRequest);

        followUpRepository.save(followUp);

        return followUpMapper.toResponse(followUp);

    }


    public List<FollowUpBasicResponse> getLeadFollowUpList(Long leadId){
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(()-> new ResourceNotFoundException("Lead not found with id : "+leadId));

        List<FollowUp> followUpList = followUpRepository.findByLead(lead);

        List<FollowUpBasicResponse> followUpBasicResponses = followUpMapper.toResponses(followUpList);

        return followUpBasicResponses;
    }

    public List<FollowUpSummaryResponse> getUpcomingFollowUps(Long userId){
        LocalDate today = LocalDate.now();

        List<FollowUp> followUpList = followUpRepository.getUpcomingFollowUpsByFilter(userId,today, FollowUpStatus.ONGOING);

        List<FollowUpSummaryResponse> followUpSummaryRespons = convertFollowListIntoFollowUpResponseList(followUpList);


        return followUpSummaryRespons;
    }

    public List<FollowUpSummaryResponse> getPastFollowUps(Long userId){
        LocalDate today = LocalDate.now();

        List<FollowUp> followUpList = followUpRepository.getPastFollowUps(userId);

        List<FollowUpSummaryResponse> followUpSummaryResponses = convertFollowListIntoFollowUpResponseList(followUpList);

        return followUpSummaryResponses;
    }

    public List<FollowUpSummaryResponse> getMissedFollowUps(Long userId){
        LocalDate today = LocalDate.now();

        List<FollowUp> followUpList = followUpRepository.getMissedFollowUps(userId);

        List<FollowUpSummaryResponse> followUpSummaryResponses = convertFollowListIntoFollowUpResponseList(followUpList);

        return followUpSummaryResponses;
    }

    public List<FollowUpSummaryResponse> getTodayFollowUps(Long userId){
        LocalDate today = LocalDate.now();

        List<FollowUp> followUpList = followUpRepository.getTodayFollowUps(userId);

        List<FollowUpSummaryResponse> followUpSummaryResponses = convertFollowListIntoFollowUpResponseList(followUpList);

        return followUpSummaryResponses;
    }


    private List<FollowUpSummaryResponse> convertFollowListIntoFollowUpResponseList(List<FollowUp> followUpList){
        List<FollowUpSummaryResponse> followUpSummaryRespons = followUpList.stream()
                .map(followUp -> convertFollowUpIntoResponse(followUp))
                .toList();

        return followUpSummaryRespons;
    }

    private FollowUpSummaryResponse convertFollowUpIntoResponse(FollowUp followUp){

        // Lead lead = followUp.getNote().getLead();
        Lead lead = followUp.getLead();
        Need need = lead.getNeed();
        EndUserDetails userDetails = lead.getEndUserDetails();

        FollowUpSummaryResponse followUpSummaryResponse = new FollowUpSummaryResponse(
                lead.getLeadId(),
                lead.getLeadReferenceNo(),
                userDetails.getEndUserId(),
                userDetails.getUserName(),
                userDetails.getEndUserContactNo(),
                userDetails.getEndUserMailId(),
                need.getNeedId(),
                need.getSource(),
                need.getDestination(),
                need.getMovingDateAndTime(),
                need.getReceivingDateAndTime(),
                need.getCommodity(),
                need.getCarTransport(),
                need.getCarMovingDate(),
                need.getCarMovingTime(),
                need.getCarReceivingDate(),
                need.getCarReceivingTime(),
                followUp.getFollowUpDate(),
                followUp.getFollowUpTime(),
                followUp.getNextFollowUpDate(),
                followUp.getFollowUpTime(),
                followUp.getOutcome(),
                followUp.getRemark()
                // ChronoUnit.DAYS.between(followUp.getFollowUpDate(),LocalDate.now())
        );

        return followUpSummaryResponse;
    }
}
