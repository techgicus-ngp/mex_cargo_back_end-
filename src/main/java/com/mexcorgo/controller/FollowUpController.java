package com.mexcorgo.controller;


import com.mexcorgo.dto.FollowUpDto.FollowUpSummaryResponse;
import com.mexcorgo.service.FollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FollowUpController {

    private FollowUpService followUpService;

    @Autowired
    public FollowUpController(FollowUpService followUpService) {
        this.followUpService = followUpService;
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
