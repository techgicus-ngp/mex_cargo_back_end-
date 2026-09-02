package com.mexcorgo.controller;


import com.mexcorgo.dto.FollowUpDto.FollowUpBasicResponse;
import com.mexcorgo.dto.FollowUpDto.FollowUpRequest;
import com.mexcorgo.dto.FollowUpDto.FollowUpSummaryResponse;

import com.mexcorgo.service.lead.followUp.FollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FollowUpController {

    private FollowUpService followUpService;

    @Autowired
    public FollowUpController(FollowUpService followUpService) {
        this.followUpService = followUpService;
    }

    @PostMapping("/add/lead/{leadId}/followups")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public ResponseEntity<FollowUpBasicResponse> addFollowUp(
            @PathVariable Long leadId,
            @RequestBody FollowUpRequest request
    ) {
        return ResponseEntity.ok(followUpService.addFollowUp(leadId,request));
    }

    @GetMapping("/get/lead/{leadId}/followUp")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public ResponseEntity<List<FollowUpBasicResponse>> getFollowUpListByLead(@PathVariable("leadId")Long leadId){
        return ResponseEntity.ok(followUpService.getLeadFollowUpList(leadId));
    }


    @GetMapping("/user/{userId}/upcoming-followups")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public List<FollowUpSummaryResponse> getUpcomingFollowUps(@PathVariable Long userId){
        return followUpService.getUpcomingFollowUps(userId);
    }


    @GetMapping("/user/{userId}/past-followups")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public List<FollowUpSummaryResponse> getPastFollowUps(@PathVariable Long userId){
        return followUpService.getPastFollowUps(userId);
    }


    @GetMapping("/user/{userId}/missed-followups")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public List<FollowUpSummaryResponse> getMissedFollowUps(@PathVariable Long userId){
        return followUpService.getMissedFollowUps(userId);
    }

    @GetMapping("/user/{userId}/today-followups")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public List<FollowUpSummaryResponse> getTodayFollowUps(@PathVariable Long userId){
        return followUpService.getTodayFollowUps(userId);
    }

}
