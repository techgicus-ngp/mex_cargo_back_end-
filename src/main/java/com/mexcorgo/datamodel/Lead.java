package com.mexcorgo.datamodel;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mexcorgo.component.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "leads", indexes = {
        @Index(name = "idx_user_id_date", columnList = "user_user_id, leadDate")
})
@DynamicUpdate
public class Lead {

       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long leadId;
       private String leadReferenceNo;
       private LocalDate leadDate;
       private LocalTime leadTime;

       @Enumerated(EnumType.STRING)
       private WayOfLead wayOfLead;

       @Enumerated(EnumType.STRING)
       private ModeOfCommunication modeOfCommunication;

       @Enumerated(EnumType.STRING)
       private DataReference dataReference;

       private String dataReferenceNo;


       @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
       @JoinColumn(name = "company_id")
       @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
       private Company company;

       @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
       @JoinColumn(name = "company_employee_id")
       @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
       private CompanyEmployee companyEmployee;

       @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
       @JoinColumn(name = "end_user_id")
       @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
       private EndUserDetails endUserDetails;

       @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
       @JoinColumn(name = "need_id")
       @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
       private Need need;

//       @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
//       @JoinColumn(name = "note_id")
//       @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//       private Note note;

       @Builder.Default
       @OneToMany(mappedBy = "lead",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
       @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
       private List<FollowUp> followUpList = new ArrayList<>();

       @Enumerated(EnumType.STRING)
       private FollowUpStatus followUpStatus;

       @ManyToOne(fetch = FetchType.LAZY)
       @JsonIgnore
       private User user;

       @Column(nullable = false)
       private Boolean isQuatationCreated=false;

       @OneToOne(mappedBy = "lead", cascade = CascadeType.ALL)
       @JsonIgnore
       private PostLeadDetails postLeadDetails;


       @Column(nullable = false)
       private Boolean isQuatationSendToUser = false;


       private LocalDate leadCreatedDate;
       private LocalTime leadCreatedTime;

       private LocalDate leadCompletedDate;

//       @Enumerated(EnumType.STRING)
//       @Column(nullable = false,length = 50)
//       private LeadStage currentStage;


       @Enumerated(EnumType.STRING)
       @Column(nullable = false,length = 50)
       private LeadStatus currentStatus;

       private String sector;

       private String subSector;

       @PrePersist
       public void setDefaults() {
              if (isQuatationCreated == null) {
                     isQuatationCreated = false;
              }
              if (isQuatationSendToUser == null) {
                     isQuatationSendToUser = false;
              }
       }

}
