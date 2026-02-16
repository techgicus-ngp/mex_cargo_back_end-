package com.mexcorgo.dto.LeadTrackingDto;

import com.mexcorgo.component.LeadStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;


@Getter @Setter
public class ProgressStep {
    private LeadStatus code;
    private String label;
    private boolean completed;
    private ZonedDateTime completedAt;
}
