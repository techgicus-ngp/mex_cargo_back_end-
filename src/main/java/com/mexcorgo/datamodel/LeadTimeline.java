package com.mexcorgo.datamodel;


import com.mexcorgo.component.LeadActionType;
import com.mexcorgo.component.LeadStage;
import com.mexcorgo.component.LeadStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class LeadTimeline {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timelineId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Lead lead;

//    @Enumerated(EnumType.STRING)
//    private LeadStage fromStage;
//
//    @Enumerated(EnumType.STRING)
//    private LeadStage toStage;
//
//    @Enumerated(EnumType.STRING)
//    private LeadStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private LeadActionType actionType;

    private String remark;

    @ManyToOne(fetch = FetchType.LAZY)
    private User actionBy;

    private ZonedDateTime actionAt;
}
