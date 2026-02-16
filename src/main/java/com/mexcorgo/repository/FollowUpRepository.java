package com.mexcorgo.repository;


import com.mexcorgo.component.FollowUpStatus;
import com.mexcorgo.datamodel.FollowUp;
import com.mexcorgo.datamodel.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUp,Long> {


    @Query("""
SELECT f
FROM FollowUp f
WHERE f.user.userId = :userId
AND NOT EXISTS (
    SELECT 1
    FROM FollowUp f2
    WHERE f2.lead.id = f.lead.id
    AND f2.user.userId = :userId
    AND (
        f2.followUpDate > f.followUpDate
        OR (
            f2.followUpDate = f.followUpDate
            AND f2.followUpTime > f.followUpTime
        )
    )
)
AND f.nextFollowUpDate >= :date
AND f.followUpStatus = :status
ORDER BY f.nextFollowUpDate ASC
""")
    List<FollowUp> getUpcomingFollowUpsByFilter(
            @Param("userId") Long userId,
            @Param("date") LocalDate date,
            @Param("status") FollowUpStatus status
    );




    @Query("""
SELECT f
FROM FollowUp f
WHERE f.user.userId = :userId
AND EXISTS (
    SELECT 1
    FROM FollowUp f2
    WHERE f2.lead.id = f.lead.id
    AND f2.user.userId = :userId
    AND (
        f2.followUpDate > f.followUpDate
        OR (
            f2.followUpDate = f.followUpDate
            AND f2.followUpTime > f.followUpTime
        )
    )
)
ORDER BY f.followUpDate DESC, f.followUpTime DESC
""")
    List<FollowUp> getPastFollowUps(@Param("userId") Long userId);


    @Query("""
SELECT f
FROM FollowUp f
WHERE f.user.userId = :userId
AND f.lead.followUpStatus != 'COMPLETED'
AND f.nextFollowUpDate < CURRENT_DATE
AND NOT EXISTS (
    SELECT 1
    FROM FollowUp f2
    WHERE f2.lead.id = f.lead.id
    AND f2.user.userId = :userId
    AND (
        f2.followUpDate > f.followUpDate
        OR (
            f2.followUpDate = f.followUpDate
            AND f2.followUpTime > f.followUpTime
        )
    )
)
ORDER BY f.nextFollowUpDate ASC
""")
    List<FollowUp> getMissedFollowUps(Long userId);


    @Query("""
SELECT f
FROM FollowUp f
WHERE f.user.userId = :userId
AND f.nextFollowUpDate = CURRENT_DATE
AND f.lead.followUpStatus != 'COMPLETED'
AND NOT EXISTS (
    SELECT 1 FROM FollowUp f2
    WHERE f2.lead.id = f.lead.id
    AND f2.followUpDate > f.followUpDate
)
""")
    List<FollowUp> getTodayFollowUps(Long userId);



    @Query("SELECT MAX(f.followUpDate) FROM FollowUp f WHERE f.lead = :lead ")
    LocalDate findMaxFollowUpDateByLead(Lead lead);


    List<FollowUp> findByLead(Lead lead);
}
