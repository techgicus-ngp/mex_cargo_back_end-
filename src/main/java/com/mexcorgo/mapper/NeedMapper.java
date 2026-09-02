package com.mexcorgo.mapper;

import com.mexcorgo.datamodel.Need;
import com.mexcorgo.dto.request.NeedRequest;
import com.mexcorgo.dto.response.NeedResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NeedMapper {


    Need converNeedResponseDtoIntoNeed(NeedResponseDto needResponseDto);

    Need convertNeedRequestIntoNeed(NeedRequest needRequest);

    void updateEntity(com.mexcorgo.dto.needDto.NeedRequest needRequest, @MappingTarget Need need);
}
