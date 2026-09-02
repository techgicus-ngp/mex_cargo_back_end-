package com.mexcorgo.dto.leadDto;

import com.mexcorgo.component.DataReference;
import com.mexcorgo.component.ModeOfCommunication;
import com.mexcorgo.component.WayOfLead;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
public class LeadRequest {
    private LocalDate date;
    private LocalTime time;
    private ModeOfCommunication modeOfCommunication;
    private WayOfLead wayOfLead;
    private DataReference dataReference;
    private String sector;
    private String subSector;
}
