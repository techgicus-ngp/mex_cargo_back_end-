package com.mexcorgo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mexcorgo.component.DataReference;
import com.mexcorgo.component.FollowUpStatus;
import com.mexcorgo.component.ModeOfCommunication;
import com.mexcorgo.component.WayOfLead;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalTime;



@Data
public class LeadResponseDto{
    private Long leadId;
    private String dataReferenceNo;
    private String departmentName;
    private String leadReferenceNo;
    private LocalDate leadDate;
    private LocalTime leadTime;
    private WayOfLead wayOfLead;
    private ModeOfCommunication modeOfCommunication;
    private DataReference dataReference;
    private Boolean isQuatationCreated;
    private Boolean isQuatationSendToUser;
    private FollowUpStatus followUpStatus;
    private Boolean isVerbalConfirmationTaskCompleted;
    private LocalDate leadCreatedDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime leadCreatedTime;
    private String sector;

    private String subSector;


    public LeadResponseDto(Long leadId, String dataReferenceNo, String departmentName, String leadReferenceNo, LocalDate leadDate, LocalTime leadTime, WayOfLead wayOfLead, ModeOfCommunication modeOfCommunication, DataReference dataReference, Boolean isQuatationCreated, Boolean isQuatationSendToUser, FollowUpStatus followUpStatus, Boolean isVerbalConfirmationTaskCompleted, LocalDate leadCreatedDate, LocalTime leadCreatedTime, String sector, String subSector) {
        this.leadId = leadId;
        this.dataReferenceNo = dataReferenceNo;
        this.departmentName = departmentName;
        this.leadReferenceNo = leadReferenceNo;
        this.leadDate = leadDate;
        this.leadTime = leadTime;
        this.wayOfLead = wayOfLead;
        this.modeOfCommunication = modeOfCommunication;
        this.dataReference = dataReference;
        this.isQuatationCreated = isQuatationCreated;
        this.isQuatationSendToUser = isQuatationSendToUser;
        this.followUpStatus = followUpStatus;
        this.isVerbalConfirmationTaskCompleted = isVerbalConfirmationTaskCompleted;
        this.leadCreatedDate = leadCreatedDate;
        this.leadCreatedTime = leadCreatedTime;
        this.sector = sector;
        this.subSector = subSector;
    }
}
