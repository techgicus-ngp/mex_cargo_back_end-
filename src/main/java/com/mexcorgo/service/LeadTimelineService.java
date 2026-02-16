package com.mexcorgo.service;

import com.mexcorgo.component.LeadActionType;
import com.mexcorgo.component.LeadStage;
import com.mexcorgo.component.LeadStatus;
import com.mexcorgo.datamodel.Lead;
import com.mexcorgo.datamodel.LeadTimeline;
import com.mexcorgo.datamodel.User;
import com.mexcorgo.repository.LeadTimelineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;


@Service
public class LeadTimelineService {

     private LeadTimelineRepository timelineRepository;

    @Autowired
    public LeadTimelineService(LeadTimelineRepository timelineRepository) {
        this.timelineRepository = timelineRepository;
    }


    public LeadTimeline setLeadTimeLineFieldsAndSave(
            Lead lead,
//            LeadStage fromStage,
//            LeadStage toStage,
//            LeadStatus status,
            String remark,
            User actionBy,
            ZonedDateTime actionAt,
            LeadActionType actionType){

        LeadTimeline timeline = new LeadTimeline();
        timeline.setLead(lead);
//        timeline.setFromStage(fromStage);
//        timeline.setToStage(toStage);
//        timeline.setStatus(status);
        timeline.setRemark(remark);
        timeline.setActionBy(actionBy);
        timeline.setActionAt(actionAt);
        timeline.setActionType(actionType);

        timelineRepository.save(timeline);

        return timeline;
    }



}
