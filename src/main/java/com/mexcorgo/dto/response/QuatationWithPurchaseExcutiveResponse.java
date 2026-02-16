package com.mexcorgo.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuatationWithPurchaseExcutiveResponse {

    private Long quatationId;
    private LocalDate requiredQuatationDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime requiredQuatationTime;
    private LocalDate forwardQuatationDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime forwardQuationTime;
    private String quatationReferenceNo;
}
