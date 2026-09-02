package com.mexcorgo.mapper;

import com.mexcorgo.datamodel.Company;
import com.mexcorgo.dto.companyDto.CompanyRequest;
import com.mexcorgo.dto.response.CompanyResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    Company toEntity(CompanyRequest request);

    CompanyRequest toRequest(Company company);

    CompanyResponseDto toResponseDto(Company company);

    void updateEntity(CompanyRequest request, @MappingTarget Company company);

}
