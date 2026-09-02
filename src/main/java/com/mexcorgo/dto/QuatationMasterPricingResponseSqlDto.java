package com.mexcorgo.dto;


import com.mexcorgo.component.Commodity;
import com.mexcorgo.component.OtherServices;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuatationMasterPricingResponseSqlDto {
    private Long leadId;
    private String leadReferenceNo;
    private String source;
    private String destination;
    private Commodity commodity;
    private String size;
    private String weight;
    private String typeOfTransportation;
    private Long quatationId;
    private LocalDateTime movingDateAndTime;
    private OtherServices otherServices;
    private String carTransport;
    private LocalDate carMovingDate;
    private LocalTime carMovingTime;
    private LocalDate quatationRequiredDate;
    private LocalTime quatationRequiredTime;
    private String quatationReferenceNo;
    private Long masterId;
    private String associateCode;
    private String serviceSector;
    private String companyName;
    private String contactName;
    private String contactNumber;
    private String emailId;
    private Integer grade;
    private String location;
    private String hub;
    private String state;
    private Double originalPackageCost;
    private Double originalTrsCost;
    private Double originalCarServiceCost;
    private Double originalAdditionalCost;
    private Double finalPackageCost;
    private Double finalTrsCost;
    private Double finalCarServiceCost;
    private Double finalAdditionalCost;


//    public QuatationMasterPricingResponseSqlDto(Long leadId, String leadReferenceNo, String source, String destination, Commodity commodity, String size, String weight, String typeOfTransporatation, Long quatationId, LocalDateTime movingDateAndTime, OtherServices otherServices, String carTransport, LocalDate carMovingDate, LocalTime carMovingTime, LocalDate quatationRequiredDate, LocalTime quatationRequiredTime, String quatationReferenceNo, Long masterId, String associateCode, String serviceSector, String companyName, String contactName, String contactNumber, String emailId, Integer grade, String location, String hub, String state, Double originalPackageCost, Double originalTrsCost, Double originalCarServiceCost, Double originalAdditionalCost, Double finalPackageCost, Double finalTrsCost, Double finalCarServiceCost, Double finalAdditionalCost) {
//        this.leadId = leadId;
//        this.leadReferenceNo = leadReferenceNo;
//        this.source = source;
//        this.destination = destination;
//        this.commodity = commodity;
//        this.size = size;
//        this.weight = weight;
//        this.typeOfTransporatation = typeOfTransporatation;
//        this.quatationId = quatationId;
//        this.movingDateAndTime = movingDateAndTime;
//        this.otherServices = otherServices;
//        this.carTransport = carTransport;
//        this.carMovingDate = carMovingDate;
//        this.carMovingTime = carMovingTime;
//        this.quatationRequiredDate = quatationRequiredDate;
//        this.quatationRequiredTime = quatationRequiredTime;
//        this.quatationReferenceNo = quatationReferenceNo;
//        this.masterId = masterId;
//        this.associateCode = associateCode;
//        this.serviceSector = serviceSector;
//        this.companyName = companyName;
//        this.contactName = contactName;
//        this.contactNumber = contactNumber;
//        this.emailId = emailId;
//        this.grade = grade;
//        this.location = location;
//        this.hub = hub;
//        this.state = state;
//        this.originalPackageCost = originalPackageCost;
//        this.originalTrsCost = originalTrsCost;
//        this.originalCarServiceCost = originalCarServiceCost;
//        this.originalAdditionalCost = originalAdditionalCost;
//        this.finalPackageCost = finalPackageCost;
//        this.finalTrsCost = finalTrsCost;
//        this.finalCarServiceCost = finalCarServiceCost;
//        this.finalAdditionalCost = finalAdditionalCost;
//    }
}
