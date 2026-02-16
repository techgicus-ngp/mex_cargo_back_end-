package com.mexcorgo.dto.FollowUpDto;

import com.mexcorgo.component.FollowUpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FollowUpRequest {
    private LocalDate followUpDate;

    private LocalTime followUpTime;

    private Integer rating;

    private String outcome;

    private LocalDate nextFollowUpDate;

    private LocalTime nextFollowUpTime;

    private FollowUpStatus followUpStatus;

    private String remark;
}
