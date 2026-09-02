package com.mexcorgo.dto.companyEmployeeDto;

import com.mexcorgo.component.ModeOfCommunication;
import lombok.Data;

@Data
public class CompanyEmployeeResponse {
    private Long companyEmployeeId;
    private String employeeName;
    private String division;
    private String department;
    private String designation;
    private String contactNo;
    private String landLineNo;
    private String mailId;
    private ModeOfCommunication modeOfContact;
    private boolean inBound;
    private boolean outBound;
}
