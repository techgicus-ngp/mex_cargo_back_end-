package com.mexcorgo.dto.endUserDetailsDto;

import com.mexcorgo.component.ModeOfCommunication;
import lombok.Data;

@Data
public class EndUserDetailsResponse {
    private Long endUserId;
    private String userName;
    private String division;
    private String endUserDepartment;
    private String endUserDesignation;
    private String endUserContactNo;
    private String endUserLandLineNo;
    private String endUserMailId;
    private ModeOfCommunication modeOfContact;
    private boolean inBound;
    private boolean outBound;
}
