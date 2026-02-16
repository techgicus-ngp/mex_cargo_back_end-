package com.mexcorgo.dto.LeadTrackingDto;

import com.mexcorgo.component.LeadActionType;
import com.mexcorgo.component.LeadStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter @Setter
public class LeadTrackingResponse {
    private String leadReferenceNo;
    private String currentStage;
    private LeadActionType lastActivity;
    private List<ProgressStep> progressSteps;
    private List<TimelineResponse> timeline;
}
