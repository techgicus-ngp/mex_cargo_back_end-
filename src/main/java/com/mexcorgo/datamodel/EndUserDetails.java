package com.mexcorgo.datamodel;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mexcorgo.component.ModeOfCommunication;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class EndUserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Lead lead;
}
