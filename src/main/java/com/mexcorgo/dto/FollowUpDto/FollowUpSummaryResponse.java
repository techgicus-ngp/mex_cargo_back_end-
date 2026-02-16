package com.mexcorgo.dto.FollowUpDto;

import com.mexcorgo.component.Commodity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record FollowUpSummaryResponse(Long leadId,
                                      String leadReferenceNo,
                                      Long endUserId,
                                      String userName,
                                      String contactNo,
                                      String mailId,
                                      Long needId,
                                      String source,
                                      String destination,
                                      LocalDateTime movingDateAndTime,
                                      LocalDateTime receivingDateAndTime,
                                      Commodity commodity,
                                      String carTransport,
                                      LocalDate carMovingDate,
                                      LocalTime carMovingTime,
                                      LocalDate carReceivingDate,
                                      LocalTime carReceivingTime,
                                      LocalDate followUpDate,
                                      LocalTime followUpTime,
                                      LocalDate nextFollowUpDate,
                                      LocalTime nextFollowUpTime,
                                      String outcome,
                                      String remark)
                                      //Long daysOverdue) {
{
}
