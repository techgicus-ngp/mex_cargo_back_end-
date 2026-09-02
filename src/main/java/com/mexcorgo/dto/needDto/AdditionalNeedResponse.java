package com.mexcorgo.dto.needDto;

import lombok.Data;

@Data
public class AdditionalNeedResponse {
    private Long additionalNeedId;
    private String articleName;
    private String articleValue;
    private String articleDimension;
    private String articleWeight;
}
