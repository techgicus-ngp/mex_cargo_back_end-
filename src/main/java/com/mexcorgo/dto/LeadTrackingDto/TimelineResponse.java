package com.mexcorgo.dto.LeadTrackingDto;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;


@Getter @Setter
public class TimelineResponse {
    private String action;
    private String remark;
    private String by;
    private ZonedDateTime at;
}
