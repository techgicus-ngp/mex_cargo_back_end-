package com.mexcorgo.mapper;

import com.mexcorgo.datamodel.CompanyEmployee;
import com.mexcorgo.dto.companyEmployeeDto.CompanyEmployeeRequest;
import com.mexcorgo.dto.response.CompanyEmployeeResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CompanyEmployeeMapper {

    CompanyEmployee toEntity(CompanyEmployeeRequest companyEmployeeRequest);

    void updateEntity(CompanyEmployeeRequest request, @MappingTarget CompanyEmployee employee);

   // CompanyEmployee converCompanyEmployeeResponseDtoInoCompanyEmployee(CompanyEmployeeResponseDto companyEmployeeResponseDto);
}
