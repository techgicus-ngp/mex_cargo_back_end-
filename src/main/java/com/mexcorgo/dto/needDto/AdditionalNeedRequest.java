package com.mexcorgo.dto.needDto;

import lombok.Data;

@Data
public class AdditionalNeedRequest {
    private String articleName;
    private String articleValue;
    private String articleDimension;
    private String articleWeight;
}
