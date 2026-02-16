package com.mexcorgo.datamodel;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class MasterQuatationResponse {

       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long masterQuatationResponseId;


    @ManyToOne
    @JoinColumn(name = "quatation_master_id", nullable = false)
    @JsonIgnore
    private QuatationMaster quatationMaster;

    // ✅ WHO ENTERED THE RESPONSE
    @ManyToOne
    @JoinColumn(name = "response_added_by_user_id", nullable = false)
    @JsonIgnore
    private User responseAddedBy;

    // ✅ WHO modified THE RESPONSE
    @ManyToOne
    @JoinColumn(name = "last_modified_by_user_id")
    @JsonIgnore
    private User lastModifiedBy;

    private Double originalPackageCost;

    private Double originalTrsCost;

    private Double originalCarServiceCost;

    private Double originalAdditionalServiceCost;

    private Double finalPackageCost;

    private Double finalTrsCost;

    private Double finalCarServiceCost;

    private Double finalAdditionalServiceCost;

    private LocalDateTime responseDate;

    private ZonedDateTime lastModifiedAt;

    private ZonedDateTime createdAt;

    public MasterQuatationResponse(Long masterQuatationResponseId, Double originalPackageCost, Double originalTrsCost, Double originalCarServiceCost, Double originalAdditionalServiceCost, Double finalPackageCost, Double finalTrsCost, Double finalCarServiceCost, Double finalAdditionalServiceCost, LocalDateTime responseDate) {
        this.masterQuatationResponseId = masterQuatationResponseId;
        this.originalPackageCost = originalPackageCost;
        this.originalTrsCost = originalTrsCost;
        this.originalCarServiceCost = originalCarServiceCost;
        this.originalAdditionalServiceCost = originalAdditionalServiceCost;
        this.finalPackageCost = finalPackageCost;
        this.finalTrsCost = finalTrsCost;
        this.finalCarServiceCost = finalCarServiceCost;
        this.finalAdditionalServiceCost = finalAdditionalServiceCost;
        this.responseDate = responseDate;
    }
}
