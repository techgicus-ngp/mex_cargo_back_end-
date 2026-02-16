package com.mexcorgo.controller;


import com.mexcorgo.dto.LeadTrackingDto.LeadTrackingResponse;
import com.mexcorgo.service.LeadTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LeadTrackingController {

    private final LeadTrackingService leadTrackingService;

    @GetMapping("/lead/{leadId}/track")
    public LeadTrackingResponse trackLead(@PathVariable Long leadId){
        return leadTrackingService.trackLead(leadId);
    }
}
