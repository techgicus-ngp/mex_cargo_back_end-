package com.mexcorgo.dto.needDto;

import com.mexcorgo.component.Commodity;
import com.mexcorgo.component.OtherServices;
import com.mexcorgo.component.RiskCoverageGood;
import com.mexcorgo.dto.request.AdditionalNeedRequest;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Data
public class NeedRequest {
    private String source;
    private String destination;
    private String originFloorNo;
    private String destinationFloorNo;
    private String originDetailsAddress;
    private String destinationDetailsAddress;
    private Boolean isLiftAvailableInOrigin;
    private Boolean isLiftAvailableInDestination;
    private String specialService;
    private String secondaryVehicle;
    private String movingDateAndTime;
    private String receivingDateTime;
    private Commodity commodity;
    private String size;
    private String weight;
    private String secondWeightValue;
    private String overAllWeightValue;
    private String typeOfTransportation;
    private String sizeOfTransportation;
    private String preferredRoot;
    private String commodityValue;
    private String vehicleValue;
    private String goodsTransport;
    private String carTransport;
    private LocalDate carMovingDate;
    private LocalTime carMovingTime;
    private LocalDate carReceivingDate;
    private LocalTime carReceivingTime;
    private String whenWeGetGoods;
    private String anyThingElseRatherThanGood;
    private String anyWarehouseFacilityRatherThanThisThings;
    private RiskCoverageGood riskCoverageGood;
    private OtherServices otherServices;
    private List<AdditionalNeedRequest> additionalNeed;

}
