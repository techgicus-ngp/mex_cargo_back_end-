package com.mexcorgo.service;


import com.mexcorgo.component.FollowUpStatus;
import com.mexcorgo.datamodel.EndUserDetails;
import com.mexcorgo.datamodel.FollowUp;
import com.mexcorgo.datamodel.Lead;
import com.mexcorgo.datamodel.Need;
import com.mexcorgo.dto.FollowUpDto.FollowUpSummaryResponse;
import com.mexcorgo.repository.FollowUpRepository;
import com.mexcorgo.repository.LeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FollowUpService {

           private LeadRepository leadRepository;
           private FollowUpRepository followUpRepository;

    @Autowired
    public FollowUpService(LeadRepository leadRepository, FollowUpRepository followUpRepository) {
        this.leadRepository = leadRepository;
        this.followUpRepository = followUpRepository;
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
                userDetails.getContactNo(),
                userDetails.getMailId(),
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
