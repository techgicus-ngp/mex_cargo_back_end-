package com.mexcorgo.datamodel;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mexcorgo.component.ModeOfCommunication;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;
    private String companyName;
    private String companySector;
    private String subSector;
    private String product;
    private String vendor_code;
    private LocalDate vendorCodingDate;
    private String category;
    private String factories;
    private String offices;
    private String office1;
    private String office2;
    private String office3;
    private String monthlyVolume;
    private String yearlyVolume;
    private String companySetUp;
    private String location;
    private String pincode;
    private String landmark;
    private String address1;
    private String address2;
    private String address3;
    private String state;
    private String minorHub;
    private String distanceFromMinorHub;
    private String majorHub;
    private String distanceFromMajorHub;
    private String website;
    private String email;
    private String phone1;
    private String phone2;
    private String phone3;
    private LocalDate date;
    private ModeOfCommunication modeOfContact;
    private boolean inBound;
    private boolean outBound;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Lead lead;
}
