package com.mexcorgo.dto.request;

import lombok.*;

@Data
public class AdditionalNeedRequest {
    private String articleName;
    private String articleValue;
    private String articleDimension;
    private String articleWeight;
}
