package com.mexcorgo.mapper;

import com.mexcorgo.datamodel.FollowUp;
import com.mexcorgo.dto.FollowUpDto.FollowUpBasicResponse;
import com.mexcorgo.dto.FollowUpDto.FollowUpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FollowUpMapper {

    FollowUp toEntity(FollowUpRequest request);

    FollowUp toEntity(FollowUpBasicResponse response);

   FollowUpBasicResponse toResponse(FollowUp followUp);

    List<FollowUpBasicResponse> toResponses(List<FollowUp> followUps);

    void updateFollowUp(FollowUpBasicResponse response, @MappingTarget FollowUp followUp);

}
