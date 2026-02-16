package com.mexcorgo.repository;

import com.mexcorgo.datamodel.LeadTimeline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadTimelineRepository extends JpaRepository<LeadTimeline,Long> {
   List<LeadTimeline> findByLead_LeadIdOrderByActionAtAsc(Long leadId);
}
