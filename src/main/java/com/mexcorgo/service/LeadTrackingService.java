package com.mexcorgo.service;


import com.mexcorgo.component.LeadActionType;
import com.mexcorgo.component.LeadStatus;
import com.mexcorgo.datamodel.Lead;
import com.mexcorgo.datamodel.LeadTimeline;
import com.mexcorgo.dto.LeadTrackingDto.LeadTrackingResponse;
import com.mexcorgo.dto.LeadTrackingDto.ProgressStep;
import com.mexcorgo.dto.LeadTrackingDto.TimelineResponse;
import com.mexcorgo.exception.ResourceNotFoundException;
import com.mexcorgo.repository.LeadRepository;
import com.mexcorgo.repository.LeadTimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeadTrackingService {

       private final LeadRepository leadRepository;
       private final LeadTimelineRepository timelineRepository;


       public LeadTrackingResponse trackLead(Long leadId){
           Lead lead = leadRepository.findById(leadId)
                   .orElseThrow(()-> new ResourceNotFoundException("Lead not found with id : "+leadId));

           List<LeadTimeline> timelines =
                   timelineRepository.findByLead_LeadIdOrderByActionAtAsc(leadId);

           LeadTrackingResponse response = new LeadTrackingResponse();
           response.setLeadReferenceNo(lead.getLeadReferenceNo());
           response.setCurrentStage(String.valueOf(calculateCurrentStage(timelines)));
           response.setLastActivity(timelines.get(timelines.size()-1).getActionType());



           // Progress steps
           List<ProgressStep> steps = new ArrayList<>();
           for (LeadStatus status : LeadStatus.values()) {
               ProgressStep step = new ProgressStep();
               step.setCode(status);
               step.setLabel(status.name().replace("_", " "));

               Map<LeadActionType, LeadTimeline> timelineMap =
                       timelines.stream()
                               .collect(Collectors.toMap(
                                       LeadTimeline::getActionType,
                                       Function.identity(),
                                       (a, b) -> a   // keep first if duplicates
                               ));

               LeadActionType actionType = mapStatusToAction(status);

               if (actionType != null && timelineMap.containsKey(actionType)) {
                   LeadTimeline timeline = timelineMap.get(actionType);
                   step.setCompleted(true);
                   step.setCompletedAt(timeline.getActionAt());
               } else {
                   step.setCompleted(false);
                   step.setCompletedAt(null);
               }

               steps.add(step);

//               timelines.stream()
//                       .filter(t -> t.getActionType().name().equals(status.name()))
//                       .findFirst()
//                       .ifPresent(t -> {
//                           step.setCompleted(true);
//                           step.setCompletedAt(t.getActionAt());
//                       });
//               steps.add(step);
           }
           response.setProgressSteps(steps);


           // Timeline
           List<TimelineResponse> timelineResponses = timelines.stream().map(t -> {
               TimelineResponse tr = new TimelineResponse();
               tr.setAction(t.getActionType().name());
               tr.setRemark(t.getRemark());
               tr.setBy(t.getActionBy().getUserName());
               tr.setAt(t.getActionAt());
               return tr;
           }).toList();

           response.setTimeline(timelineResponses);

           return response;

       }


    private LeadStatus calculateCurrentStage(List<LeadTimeline> timelines) {

        return timelines.stream()
                .map(t -> mapActionToStage(t.getActionType()))
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(LeadStatus::getOrder))
                .orElse(LeadStatus.CREATED);
    }

    private LeadStatus mapActionToStage(LeadActionType action) {

        return switch (action) {
            case CREATED -> LeadStatus.CREATED;
            case SEND_TO_PURCHASE -> LeadStatus.SENT_TO_PURCHASE;
            case SEND_TO_MASTER -> LeadStatus.SENT_TO_MASTER;
            case COLLECT_MASTER_RESPONSE -> LeadStatus.MASTER_RESPONSE_RECEIVED;
            case SEND_TO_PRICING -> LeadStatus.SENT_TO_PRICING;
            case ANALYZED_PRICING -> LeadStatus.ANALYZED_PRICING;
            case SEND_TO_LEAD_CREATOR -> LeadStatus.SEND_TO_LEAD_CREATOR;
            case QUOTATION_PRICE_FINALIZED -> LeadStatus.QUOTATION_PRICE_FINALIZED;
            case SEND_TO_CUSTOMER ->  LeadStatus.SENT_TO_CUSTOMER;
            default -> null;
        };
    }

    private List<ProgressStep> buildProgressSteps(List<LeadTimeline> timelines) {

        Set<LeadStatus> completedStages =
                timelines.stream()
                        .map(t -> mapActionToStage(t.getActionType()))
                        .collect(Collectors.toSet());

        List<ProgressStep> steps = new ArrayList<>();

        for (LeadStatus status : LeadStatus.values()) {

            ProgressStep step = new ProgressStep();
            step.setCode(status);
            step.setLabel(status.name().replace("_", " "));

            if (completedStages.contains(status)) {
                step.setCompleted(true);
                step.setCompletedAt(
                        timelines.stream()
                                .filter(t -> mapActionToStage(t.getActionType()) == status)
                                .map(LeadTimeline::getActionAt)
                                .min(ZonedDateTime::compareTo)
                                .orElse(null)
                );
            } else {
                step.setCompleted(false);
            }

            steps.add(step);
        }
        return steps;
    }


    private List<TimelineResponse> buildTimeline(List<LeadTimeline> timelines) {

        return timelines.stream().map(t -> {
            TimelineResponse tr = new TimelineResponse();
            tr.setAction(t.getActionType().name());
            tr.setRemark(t.getRemark());
            tr.setBy(t.getActionBy().getUserName());
            tr.setAt(t.getActionAt());
            return tr;
        }).toList();
    }


private LeadActionType mapStatusToAction(LeadStatus status) {
        return switch (status) {
            case CREATED -> LeadActionType.CREATED;
            case SENT_TO_PURCHASE -> LeadActionType.SEND_TO_PURCHASE;
            case SENT_TO_MASTER -> LeadActionType.SEND_TO_MASTER;
            case MASTER_RESPONSE_RECEIVED -> LeadActionType.COLLECT_MASTER_RESPONSE;
            case SENT_TO_PRICING -> LeadActionType.SEND_TO_PRICING;
            case SEND_TO_LEAD_CREATOR -> LeadActionType.SEND_TO_LEAD_CREATOR;
            case QUOTATION_PRICE_FINALIZED -> LeadActionType.QUOTATION_PRICE_FINALIZED;
            case SENT_TO_CUSTOMER -> LeadActionType.SEND_TO_CUSTOMER;
//            case ANALYZED_PRICING -> LeadActionType.ANALYZED_PRICING;
//            case SENT_TO_LEAD_CREATOR -> LeadActionType.SEND_TO_LEAD_CREATOR;
//            case PRICING_COMPLETE -> LeadActionType.PRICING_COMPLETE;
//            case SENT_TO_CUSTOMER -> LeadActionType.SEND_TO_CUSTOMER;
//            case LEAD_CONFIRMED -> LeadActionType.LEAD_CONFIRMED;
//            case SENT_TO_ACCOUNT -> LeadActionType.SEND_TO_ACCOUNT;
//            case SENT_TO_PLANNING -> LeadActionType.SEND_TO_PLANNING;
//            case PAYMENT_PENDING -> LeadActionType.PAYMENT_PENDING;
//            case SERVICE_PLANNING -> LeadActionType.SERVICE_PLANNING;
//            case PROJECT_CREATED -> LeadActionType.PROJECT_CREATED;
//            case CLOSED -> LeadActionType.CLOSED;
            default -> null;
        };
    }


}

