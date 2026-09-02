package com.mexcorgo.dto.leadDto;

import com.mexcorgo.component.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class LeadResponse {
    private Long leadId;
    private String leadReferenceNo;
    private String dataReferenceNo;
    private LocalDate leadDate;
    private LocalTime leadTime;
    private ModeOfCommunication modeOfCommunication;
    private WayOfLead wayOfLead;
    private DataReference dataReference;
    private String sector;
    private String subSector;
    private FollowUpStatus followUpStatus;
    private LeadStatus currentStatus;
    private Boolean isQuotationCreated;
    private Boolean isQuotationSentToUser;
    private LocalDate leadCreatedDate;
    private LocalTime leadCreatedTime;
    private LocalDate leadCompletedDate;
}
