package com.mexcorgo.datamodel;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mexcorgo.component.ModeOfCommunication;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class CompanyEmployee {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
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

     @OneToOne(fetch = FetchType.LAZY)
     @JsonIgnore
     private Lead lead;
}
