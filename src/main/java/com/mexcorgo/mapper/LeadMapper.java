package com.mexcorgo.mapper;

import com.mexcorgo.datamodel.*;
import com.mexcorgo.dto.FollowUpDto.FollowUpBasicResponse;
import com.mexcorgo.dto.companyDto.CompanyRequest;
import com.mexcorgo.dto.companyDto.CompanyResponse;
import com.mexcorgo.dto.companyEmployeeDto.CompanyEmployeeRequest;
import com.mexcorgo.dto.companyEmployeeDto.CompanyEmployeeResponse;
import com.mexcorgo.dto.endUserDetailsDto.EndUserDetailsRequest;
import com.mexcorgo.dto.endUserDetailsDto.EndUserDetailsResponse;
import com.mexcorgo.dto.leadDto.CreateLeadRequest;
import com.mexcorgo.dto.leadDto.LeadFullResponse;
import com.mexcorgo.dto.leadDto.LeadRequest;
import com.mexcorgo.dto.leadDto.LeadResponse;
import com.mexcorgo.dto.needDto.AdditionalNeedRequest;
import com.mexcorgo.dto.needDto.NeedRequest;
import com.mexcorgo.dto.needDto.NeedResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LeadMapper {

    // ─── NESTED CONVERTERS (fixes the MapStruct error) ────────────────
    CompanyResponse             toCompanyResponse(Company company);
    CompanyEmployeeResponse     toCompanyEmployeeResponse(CompanyEmployee companyEmployee);
    EndUserDetailsResponse      toEndUserDetailsResponse(EndUserDetails endUserDetails);
    NeedResponse                toNeedResponse(Need need);
    FollowUpBasicResponse       toFollowUpBasicResponse(FollowUp followUp);

    // MapStruct auto-uses these for List<FollowUp> → List<FollowUpBasicResponse>
    List<FollowUpBasicResponse> toFollowUpBasicResponseList(List<FollowUp> followUps);

    // ─── NESTED REQUEST CONVERTERS (for toEntity) ─────────────────────
    Company             toCompanyEntity(CompanyRequest request);
    CompanyEmployee     toCompanyEmployeeEntity(CompanyEmployeeRequest request);
    EndUserDetails      toEndUserDetailsEntity(EndUserDetailsRequest request);
    AdditionalNeed      toAdditionalNeedEntity(AdditionalNeedRequest request);
    List<AdditionalNeed> toAdditionalNeedEntityList(List<AdditionalNeedRequest> requests);
    Need                toNeedEntity(NeedRequest request);

    @AfterMapping
    default void linkAdditionalNeeds(@MappingTarget Need need) {
        if (need.getAdditionalNeed() != null) {
            need.getAdditionalNeed().forEach(a -> a.setNeed(need));
        }
    }

    // ─── CREATE ───────────────────────────────────────────────────────
    @Mapping(target = "leadDate",            source = "lead.date")
    @Mapping(target = "leadTime",            source = "lead.time")
    @Mapping(target = "wayOfLead",           source = "lead.wayOfLead")
    @Mapping(target = "modeOfCommunication", source = "lead.modeOfCommunication")
    @Mapping(target = "dataReference",       source = "lead.dataReference")
    @Mapping(target = "sector",              source = "lead.sector")
    @Mapping(target = "subSector",           source = "lead.subSector")
    @Mapping(target = "company",             source = "company")
    @Mapping(target = "companyEmployee",     source = "companyEmployee")
    @Mapping(target = "need",                source = "need")
    @Mapping(target = "endUserDetails",      source = "endUserDetails")
    @Mapping(target = "leadId",                ignore = true)
    @Mapping(target = "leadReferenceNo",       ignore = true)
    @Mapping(target = "dataReferenceNo",       ignore = true)
    @Mapping(target = "user",                  ignore = true)
    @Mapping(target = "followUpList",          ignore = true)
    @Mapping(target = "followUpStatus",        ignore = true)
    @Mapping(target = "currentStatus",         ignore = true)
    @Mapping(target = "isQuatationCreated",    ignore = true)
    @Mapping(target = "isQuatationSendToUser", ignore = true)
    @Mapping(target = "leadCreatedDate",       ignore = true)
    @Mapping(target = "leadCreatedTime",       ignore = true)
    @Mapping(target = "leadCompletedDate",     ignore = true)
    @Mapping(target = "postLeadDetails",       ignore = true)
    Lead toEntity(CreateLeadRequest request);

    // ─── GET (entity → full nested response) ──────────────────────────
    @Mapping(target = "lead.leadId",               source = "leadId")
    @Mapping(target = "lead.leadReferenceNo",       source = "leadReferenceNo")
    @Mapping(target = "lead.dataReferenceNo",       source = "dataReferenceNo")
    @Mapping(target = "lead.leadDate",              source = "leadDate")
    @Mapping(target = "lead.leadTime",              source = "leadTime")
    @Mapping(target = "lead.wayOfLead",             source = "wayOfLead")
    @Mapping(target = "lead.modeOfCommunication",   source = "modeOfCommunication")
    @Mapping(target = "lead.dataReference",         source = "dataReference")
    @Mapping(target = "lead.sector",                source = "sector")
    @Mapping(target = "lead.subSector",             source = "subSector")
    @Mapping(target = "lead.followUpStatus",        source = "followUpStatus")
    @Mapping(target = "lead.currentStatus",         source = "currentStatus")
    @Mapping(target = "lead.isQuotationCreated",    source = "isQuatationCreated")
    @Mapping(target = "lead.isQuotationSentToUser", source = "isQuatationSendToUser")
    @Mapping(target = "lead.leadCreatedDate",       source = "leadCreatedDate")
    @Mapping(target = "lead.leadCreatedTime",       source = "leadCreatedTime")
    @Mapping(target = "lead.leadCompletedDate",     source = "leadCompletedDate")
    @Mapping(target = "company",                    source = "company")
    @Mapping(target = "companyEmployee",            source = "companyEmployee")
    @Mapping(target = "endUserDetails",             source = "endUserDetails")
    @Mapping(target = "need",                       source = "need")
    @Mapping(target = "followUps",                  source = "followUpList")
    LeadFullResponse toFullResponse(Lead lead);

    // ─── UPDATE lead-level fields only ────────────────────────────────
    @Mapping(target = "leadDate",            source = "leadDate")
    @Mapping(target = "leadTime",            source = "leadTime")
    @Mapping(target = "wayOfLead",           source = "wayOfLead")
    @Mapping(target = "modeOfCommunication", source = "modeOfCommunication")
    @Mapping(target = "dataReference",       source = "dataReference")
    @Mapping(target = "sector",              source = "sector")
    @Mapping(target = "subSector",           source = "subSector")
    @Mapping(target = "leadId",                ignore = true)
    @Mapping(target = "leadReferenceNo",       ignore = true)
    @Mapping(target = "dataReferenceNo",       ignore = true)
    @Mapping(target = "user",                  ignore = true)
    @Mapping(target = "followUpList",          ignore = true)
    @Mapping(target = "followUpStatus",        ignore = true)
    @Mapping(target = "currentStatus",         ignore = true)
    @Mapping(target = "isQuatationCreated",    ignore = true)
    @Mapping(target = "isQuatationSendToUser", ignore = true)
    @Mapping(target = "leadCreatedDate",       ignore = true)
    @Mapping(target = "leadCreatedTime",       ignore = true)
    @Mapping(target = "leadCompletedDate",     ignore = true)
    @Mapping(target = "postLeadDetails",       ignore = true)
    @Mapping(target = "company",               ignore = true)
    @Mapping(target = "companyEmployee",       ignore = true)
    @Mapping(target = "endUserDetails",        ignore = true)
    @Mapping(target = "need",                  ignore = true)
    void updateLeadFields(LeadResponse leadResponse, @MappingTarget Lead lead);

    // ─── SUB-ENTITY UPDATERS ──────────────────────────────────────────
    void updateCompany(CompanyResponse response,         @MappingTarget Company company);
    void updateEmployee(CompanyEmployeeResponse response, @MappingTarget CompanyEmployee employee);
    void updateEndUser(EndUserDetailsResponse response,  @MappingTarget EndUserDetails endUser);
    void updateNeed(NeedResponse response,               @MappingTarget Need need);

    // In LeadMapper.java — needed for update when sub-entity is null on lead
    Company          toCompanyEntity(CompanyResponse response);       // Response not Request
    CompanyEmployee  toCompanyEmployeeEntity(CompanyEmployeeResponse response);
    EndUserDetails   toEndUserDetailsEntity(EndUserDetailsResponse response);
    Need             toNeedEntity(NeedResponse response);
}