package com.mexcorgo.service;


import com.mexcorgo.component.LeadActionType;
import com.mexcorgo.component.LeadEvent;
import com.mexcorgo.component.LeadStatus;
import com.mexcorgo.datamodel.Lead;
import com.mexcorgo.datamodel.User;
import com.mexcorgo.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
//import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class LeadStateService {

    private final StateMachineFactory<LeadStatus, LeadEvent> factory;
      private final LeadRepository leadRepository;
      private final LeadTimelineService timelineService;

    @Transactional
    public void performAction(
            Lead lead,
            LeadEvent event,
            LeadActionType actionType,
            String remark,
           // Long actionByUserId
            User actionBy
    ) {
//        Lead lead = leadRepository.findById(leadId)
//                .orElseThrow(() -> new RuntimeException("Lead not found"));

        // 1️⃣ Build state machine
        StateMachine<LeadStatus, LeadEvent> sm =
                factory.getStateMachine(lead.getLeadId().toString());

        // 2️⃣ Restore state from DB
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(access -> {
                    access.resetStateMachine(
                            new DefaultStateMachineContext<>(
                                    lead.getCurrentStatus(),
                                    null, null, null
                            )
                    );
                });
        sm.start();

        // 3️⃣ Send event
        boolean accepted = sm.sendEvent(event);

        if (!accepted) {
            throw new IllegalStateException(
                    "Action not allowed in current state: " + lead.getCurrentStatus()
            );
        }

        // 4️⃣ Persist new state
        LeadStatus newStatus = sm.getState().getId();
        lead.setCurrentStatus(newStatus);
        leadRepository.save(lead);

        // 5️⃣ Timeline
        timelineService.setLeadTimeLineFieldsAndSave(
                lead,
//                lead.getCurrentStatus().name(),
//                newStatus.name(),
                remark,
          //      actionByUserId,
                actionBy,
                ZonedDateTime.now(ZoneId.of("Asia/Kolkata")),
                actionType
        );
    }
}
