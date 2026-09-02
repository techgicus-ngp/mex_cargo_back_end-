package com.mexcorgo.service.lead;


import com.mexcorgo.component.FollowUpStatus;
import com.mexcorgo.component.LeadActionType;
import com.mexcorgo.component.LeadStage;
import com.mexcorgo.component.LeadStatus;
import com.mexcorgo.datamodel.*;
import com.mexcorgo.dto.FollowUpDto.FollowUpBasicResponse;
import com.mexcorgo.dto.FollowUpDto.FollowUpRequest;
import com.mexcorgo.dto.companyDto.CompanyRequest;
import com.mexcorgo.dto.companyEmployeeDto.CompanyEmployeeRequest;
import com.mexcorgo.dto.leadDto.CreateLeadRequest;
import com.mexcorgo.dto.request.AddParticularsRequestDto;
import com.mexcorgo.dto.request.NeedExtraDetailsDto;
import com.mexcorgo.dto.request.NeedExtraDetailsRequestDto;
import com.mexcorgo.dto.response.*;
import com.mexcorgo.exception.*;
import com.mexcorgo.mapper.*;
import com.mexcorgo.repository.*;
import com.mexcorgo.service.LeadTimelineService;
import com.mexcorgo.service.lead.company.CompanyService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mexcorgo.dto.leadDto.*;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    private final NeedExtraDetailsRepository needExtraDetailsRepository;
    private final EndUserDetailsRepository endUserDetailsRepository;
    private final QuatationRepository quatationRepository;
    private final QuatationParticularsAmountRepository quatationParticularsAmountRepository;
    private final LeadTimelineService leadTimelineService;
    private final LeadMapper leadMapper;
    private final FollowUpMapper followUpMapper;
    private final LeadTimelineRepository leadTimelineRepository;

           Logger logger = LoggerFactory.getLogger(LeadService.class);



    @Transactional
    public Lead createLead(CreateLeadRequest leadRequest, User currentUser){

        Lead lead = leadMapper.toEntity(leadRequest);
        lead.setUser(currentUser);
        lead.setLeadReferenceNo(generateLeadReferenceNumber(currentUser.getUserId()));
        lead.setDataReferenceNo(generateDataReferenceNo(currentUser.getUserId()));
        lead.setCurrentStatus(LeadStatus.CREATED);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        lead.setLeadCreatedDate(now.toLocalDate());
        lead.setLeadCreatedTime(now.toLocalTime());

        // 3. Wire bidirectional relationships
        // Wire bidirectional relationships safely
        Optional.ofNullable(lead.getCompany()).ifPresent(c -> c.setLead(lead));
        Optional.ofNullable(lead.getCompanyEmployee()).ifPresent(e -> e.setLead(lead));
        Optional.ofNullable(lead.getEndUserDetails()).ifPresent(eu -> eu.setLead(lead));
        Optional.ofNullable(lead.getNeed()).ifPresent(n -> n.setLead(lead));

        // 4. Handle follow-up
        if(leadRequest.getFollowUp() !=null) {
            FollowUp followUp = followUpMapper.toEntity(leadRequest.getFollowUp());
            followUp.setUser(currentUser);
            followUp.setLead(lead);
            lead.setFollowUpList(List.of(followUp));
            lead.setFollowUpStatus(leadRequest.getFollowUp().getFollowUpStatus());
        }
        else{
            lead.setFollowUpList(new ArrayList<>());
        }

        // 5. Save and record timeline
        Lead saved = leadRepository.save(lead);
        leadTimelineService.setLeadTimeLineFieldsAndSave(
                saved, null, saved.getUser(),
                LocalDateTime.now().atZone(ZoneId.of("Asia/Kolkata")),
                LeadActionType.CREATED
        );

        logger.info("lead is save in the lead");

         leadTimelineService.setLeadTimeLineFieldsAndSave(
                saved,
                null,
//                LeadStage.SALES,
//                LeadStatus.CREATED,
//                "Lead created by Sales",
                saved.getUser(),
                now,
                LeadActionType.CREATED
        );

        return saved;

    }




    public Page<LeadResponseDto> getUserLeads(Long userId, int page, int size){

        //created Pageable object with page number and page size
        Pageable pageable = PageRequest.of(page,size,Sort.by(Sort.Order.desc("leadDate"),Sort.Order.desc("leadId")));

        logger.info("Fetching leads for userId: {}, page: {}, size: {}", userId, pageable.getPageNumber(), pageable.getPageSize());

        Page<LeadResponseDto> leads = leadRepository.findByUser_UserIdOrderByLeadDateDesc(userId,pageable);

        return leads;
    }


    @Transactional
    public Boolean deleteTheLead(Long leadId){
        Lead existingLead = leadRepository.findById(leadId)
                .orElseThrow(()->new UserNotFoundException("Lead is not found with id : "+leadId));

        leadTimelineRepository.deleteByLead(existingLead);
        leadRepository.delete(existingLead);

        return true;
    }

    public LeadResponseDto getLeadById(Long leadId){
      LeadResponseDto lead = leadRepository.findByLeadId(leadId);
      return lead;
    }


//    @Transactional
//    public LeadFullResponse updateLeadDetails(Long leadId,LeadResponse request){
//        Lead existingLead = leadRepository.findById(leadId)
//                .orElseThrow(()-> new UserNotFoundException("Lead is not found with id : "+leadId));
//
//        leadMapper.updateLeadFields(request,existingLead);
//
//        leadRepository.save(existingLead);
//
//        return leadMapper.toFullResponse(existingLead);
//    }


    @Transactional
    public LeadFullResponse getFullLeadDetails(Long leadId){

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(()-> new ResourceNotFoundException("Lead with id : "+leadId+" is not found"));

        return leadMapper.toFullResponse(lead);
    }

    @Transactional
    public LeadFullResponse updateFullLeadDetails(Long leadId,LeadFullResponse request){

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(()-> new ResourceNotFoundException("Lead with id : "+leadId+" is not found"));

        // 1. patch lead-level fields (ignores system fields)
        if (request.getLead() != null) {
            leadMapper.updateLeadFields(request.getLead(), lead);
        }

        // 2. patch each sub-entity in place (preserves FK, no orphans)
        if (request.getCompany() != null) {
            if (lead.getCompany() != null) {
                // Update existing company
                leadMapper.updateCompany(request.getCompany(), lead.getCompany());
            } else {
                // Company didn't exist before — create and wire it
                Company newCompany = leadMapper.toCompanyEntity(request.getCompany());
                newCompany.setLead(lead);
                lead.setCompany(newCompany);
            }
        }
        if (request.getCompanyEmployee() != null) {
            if (lead.getCompanyEmployee() != null) {
                leadMapper.updateEmployee(request.getCompanyEmployee(), lead.getCompanyEmployee());
            } else {
                CompanyEmployee newEmployee = leadMapper.toCompanyEmployeeEntity(request.getCompanyEmployee());
                newEmployee.setLead(lead);
                lead.setCompanyEmployee(newEmployee);
            }
        }

        if (request.getEndUserDetails() != null) {
            if (lead.getEndUserDetails() != null) {
                leadMapper.updateEndUser(request.getEndUserDetails(), lead.getEndUserDetails());
            } else {
                EndUserDetails newEndUser = leadMapper.toEndUserDetailsEntity(request.getEndUserDetails());
                newEndUser.setLead(lead);
                lead.setEndUserDetails(newEndUser);
            }
        }

        if (request.getNeed() != null) {
            if (lead.getNeed() != null) {
                leadMapper.updateNeed(request.getNeed(), lead.getNeed());
            } else {
                Need newNeed = leadMapper.toNeedEntity(request.getNeed());
                newNeed.setLead(lead);
                lead.setNeed(newNeed);
            }
        }

        if (request.getFollowUps() != null && !request.getFollowUps().isEmpty()) {
            for (FollowUpBasicResponse followUpResponse : request.getFollowUps()) {

                if (followUpResponse.getFollowUpId() != null) {
                    // ✅ Existing followUp — find and update in place
                    lead.getFollowUpList().stream()
                            .filter(f -> f.getFollowUpId().equals(followUpResponse.getFollowUpId()))
                            .findFirst()
                            .ifPresent(existingFollowUp ->
                                    followUpMapper.updateFollowUp(followUpResponse, existingFollowUp)
                            );
                } else {
                    // ✅ New followUp — create and wire it
                    FollowUp newFollowUp = followUpMapper.toEntity(followUpResponse);
                    newFollowUp.setLead(lead);
                    newFollowUp.setUser(lead.getUser());
                    lead.getFollowUpList().add(newFollowUp);
                }
            }

            // Update followUpStatus from the latest followUp
            lead.getFollowUpList().stream()
                    .reduce((first, second) -> second) // get last element
                    .ifPresent(latest ->
                            lead.setFollowUpStatus(latest.getFollowUpStatus())
                    );
        }


        // 3. single save cascades to all children
        Lead saved = leadRepository.save(lead);

        return leadMapper.toFullResponse(saved);


    }




    private String generateDataReferenceNo(Long userId){

        // Fetch last data reference number for this user
        String lastDataRefNo = leadRepository.findLastDataReferenceForUser(userId);

        int sequenceNumber = 1; // default first entry

        if (lastDataRefNo !=null){
            // userId length may vary, so remove userId prefix
            String lastSequenceStr = lastDataRefNo.substring(String.valueOf(userId).length());
            sequenceNumber = Integer.parseInt(lastSequenceStr) + 1;
        }

        // userId + 4 digit sequence
        return String.format("%d%04d", userId, sequenceNumber);
    }

    public String generateLeadReferenceNumber(Long userId) {
        int year = LocalDate.now().getYear() % 100; // Get last two digits of the year (e.g., 25 for 2025)

        // Fetch the latest lead for this user in the current year
        String lastLeadRefNo = leadRepository.findLastLeadReferenceForUser(userId, String.valueOf(year));

        int sequenceNumber = 1; // Default to 0001 if no leads exist for this user

        if (lastLeadRefNo != null) {
            String lastSequenceStr = lastLeadRefNo.substring(6); // Extract the last 4 digits (after year and userId)
            sequenceNumber = Integer.parseInt(lastSequenceStr) + 1;
        }

        return String.format("%02d%02d%04d", year, userId, sequenceNumber);
    }


    public QuatationParticularsAmount getParticularAmount(Long leadId){
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(()-> new ResourceNotFoundException("Lead with id : "+leadId+" is not found"));

        Quatation quatation = quatationRepository.findByLead_LeadId(lead.getLeadId());

        QuatationParticularsAmount quatationParticularsAmount = quatationParticularsAmountRepository.findByQuatation_QuatationId(quatation.getQuatationId());

        return quatationParticularsAmount;
    }

    public QuatationParticularsAmount updateParticularAmount(Long leadId, AddParticularsRequestDto particularsRequestDto){

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(()-> new ResourceNotFoundException("Lead with id : "+leadId+" is not found"));

        Quatation quatation = quatationRepository.findByLead_LeadId(lead.getLeadId());

        QuatationParticularsAmount quatationParticularsAmount = quatationParticularsAmountRepository.findByQuatation_QuatationId(quatation.getQuatationId());

        quatationParticularsAmount.setPackingAmount(particularsRequestDto.getPackingAmount());
        quatationParticularsAmount.setLoadingAmount(particularsRequestDto.getLoadingAmount());
        quatationParticularsAmount.setUnloadingAmount(particularsRequestDto.getUnloadingAmount());
        quatationParticularsAmount.setUnpackingAmount(particularsRequestDto.getUnpackingAmount());
        quatationParticularsAmount.setPackingAndLoadingAmount(particularsRequestDto.getPackingAndLoadingAmount());
        quatationParticularsAmount.setUnloadingAndUnpackingAmount(particularsRequestDto.getUnloadingAndUnpackingAmount());
        quatationParticularsAmount.setPackingAndLoadingAndUnloadingAndUnpackingAmount(particularsRequestDto.getPackingAndLoadingAndUnloadingAndUnpackingAmount());
        quatationParticularsAmount.setTransportationOfHouseholdAmount(particularsRequestDto.getTransportationOfHouseholdAmount());

        QuatationParticularsAmount saveQuatationParticularAmount = quatationParticularsAmountRepository.save(quatationParticularsAmount);

        return saveQuatationParticularAmount;

    }


    public NeedExtraDetails getExtraNeedDetails(Long leadId){
      return needExtraDetailsRepository.findByLeadLeadId(leadId)
              .orElseThrow(()-> new ResourceNotFoundException("Extra need is not present with lead id : "+leadId));
    }

    public NeedExtraDetails updateExtraNeedDetails(Long leadId,NeedExtraDetails needExtraDetails){
        NeedExtraDetails getExtraNeedDetails = needExtraDetailsRepository.findByLeadLeadId(leadId)
                .orElseThrow(()-> new ResourceNotFoundException("Extra need is not present with lead id : "+leadId));

        getExtraNeedDetails.setOriginDetailsAddress(needExtraDetails.getOriginDetailsAddress());
        getExtraNeedDetails.setDestinationDetailsAddress(needExtraDetails.getDestinationDetailsAddress());
        getExtraNeedDetails.setOriginFloorNo(needExtraDetails.getOriginFloorNo());
        getExtraNeedDetails.setDestinationFloorNo(needExtraDetails.getDestinationFloorNo());
        getExtraNeedDetails.setIsLiftAvailableInOrigin(needExtraDetails.getIsLiftAvailableInOrigin());
        getExtraNeedDetails.setIsLiftAvailableInDestination(needExtraDetails.getIsLiftAvailableInDestination());
        getExtraNeedDetails.setSpecialService(needExtraDetails.getSpecialService());
        getExtraNeedDetails.setSecondaryVehicle(needExtraDetails.getSecondaryVehicle());
        getExtraNeedDetails.setRemark(needExtraDetails.getRemark());

      NeedExtraDetails saveNeedExtraDetails = needExtraDetailsRepository.save(getExtraNeedDetails);

      return saveNeedExtraDetails;
    }






//    public NeedExtraDetails addNeedExtraDetails(Long leadId, NeedExtraDetailsDto needExtraDetailsDto){
//
//        Lead lead = leadRepository.findById(leadId)
//                .orElseThrow(()-> new ResourceNotFoundException("Lead with id : "+leadId+" is not found"));
//
//        NeedExtraDetails needExtraDetails = needExtraDetailsMapper.convertNeedExtraDetailsDtoIntoNeedExtraDetails(needExtraDetailsDto);
//        needExtraDetails.setLead(lead);
//
//        NeedExtraDetails  saveNeedExtraDetails = needExtraDetailsRepository.save(needExtraDetails);
//
//        return saveNeedExtraDetails;
//    }



//    public List<LeadResponseDto> findLeadByFollowUpDate(LocalDate followUpDate){
//        List<Lead> leads = leadRepository.findLeadByFollowUpDate(followUpDate);
//
//        List<LeadResponseDto> leadResponseDtos = leads.stream()
//                .map(lead -> {
//                    LeadResponseDto leadResponseDto = new LeadResponseDto(
//                            lead.getLeadId(),
//                            lead.getDataReferenceNo(),
//                            lead.getUser().getDepartment().getDepartmentName(),
//                            lead.getLeadReferenceNo(),
//                            lead.getLeadDate(),
//                            lead.getLeadTime(),
//                            lead.getWayOfLead(),
//                            lead.getModeOfCommunication(),
//                            lead.getDataReference(),
//                            lead.getIsQuatationCreated(),
//                            lead.getIsQuatationSendToUser(),
//                            lead.getFollowUpStatus(),
//                            null,
//                            lead.getLeadCreatedDate(),
//                            lead.getLeadCreatedTime()
//                    );
//
//                    return leadResponseDto;
//                }).toList();
//
//        return leadResponseDtos;
//    }


    public NeedExtraDetails afterLeadConfirmAddExtraNeedData(Long leadId, NeedExtraDetailsRequestDto needExtraDetailsRequestDto){
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(()-> new ResourceNotFoundException("Lead with id : "+leadId+" is not found"));

        //check lead status
        if(lead.getFollowUpStatus() == FollowUpStatus.ONGOING){
            throw new InvalidLeadStatusException("Lead is still ongoing so, we can not go futher.");
        }

//        if(lead.getNote().getFollowUpStatus() == FollowUpStatus.ONGOING){
//            throw new InvalidLeadStatusException("Lead is still ongoing so, we can not go futher.");
//        }


        Optional<NeedExtraDetails> getNeedExtraDetails = needExtraDetailsRepository.findByLeadLeadId(leadId);

        if (getNeedExtraDetails.isPresent()){
            throw new CustomException("Extra need is alredy added,for one lead you can add only one exta need");
        }

        NeedExtraDetails needExtraDetails = new NeedExtraDetails();
        needExtraDetails.setLead(lead);
        needExtraDetails.setOriginDetailsAddress(needExtraDetailsRequestDto.getOriginDetailsAddress());
        needExtraDetails.setDestinationDetailsAddress(needExtraDetailsRequestDto.getDestinationDetailsAddress());
        needExtraDetails.setOriginFloorNo(needExtraDetailsRequestDto.getOriginFloorNo());
        needExtraDetails.setDestinationFloorNo(needExtraDetailsRequestDto.getDestinationFloorNo());
        needExtraDetails.setIsLiftAvailableInOrigin(needExtraDetailsRequestDto.getIsLiftAvailableInOrigin());
        needExtraDetails.setIsLiftAvailableInDestination(needExtraDetailsRequestDto.getIsLiftAvailableInDestination());
        needExtraDetails.setSpecialService(needExtraDetailsRequestDto.getSpecialService());
        needExtraDetails.setSecondaryVehicle(needExtraDetailsRequestDto.getSecondaryVehicle());
        needExtraDetails.setRemark(needExtraDetailsRequestDto.getRemark());

        NeedExtraDetails saveNeedExtraDetails = needExtraDetailsRepository.save(needExtraDetails);

        return saveNeedExtraDetails;
    }





//    public QuatationParticularsAmount updateParticularAmount(Long leadId,AddParticularsRequestDto particularsRequestDto){
//
//        Lead lead = leadRepository.findById(leadId)
//                .orElseThrow(()-> new ResourceNotFoundException("Lead with id : "+leadId+" is not found"));
//
//        Quatation quatation = quatationRepository.findByLead_LeadId(lead.getLeadId());
//
//        QuatationParticularsAmount quatationParticularsAmount = quatationParticularsAmountRepository.findByQuatation_QuatationId(quatation.getQuatationId());
//
//        quatationParticularsAmount.setPackingAmount(particularsRequestDto.getPackingAmount());
//        quatationParticularsAmount.setLoadingAmount(particularsRequestDto.getLoadingAmount());
//        quatationParticularsAmount.setUnloadingAmount(particularsRequestDto.getUnloadingAmount());
//        quatationParticularsAmount.setUnpackingAmount(particularsRequestDto.getUnpackingAmount());
//        quatationParticularsAmount.setPackingAndLoadingAmount(particularsRequestDto.getPackingAndLoadingAmount());
//        quatationParticularsAmount.setUnloadingAndUnpackingAmount(particularsRequestDto.getUnloadingAndUnpackingAmount());
//        quatationParticularsAmount.setPackingAndLoadingAndUnloadingAndUnpackingAmount(particularsRequestDto.getPackingAndLoadingAndUnloadingAndUnpackingAmount());
//        quatationParticularsAmount.setTransportationOfHouseholdAmount(particularsRequestDto.getTransportationOfHouseholdAmount());
//
//        QuatationParticularsAmount saveQuatationParticularAmount = quatationParticularsAmountRepository.save(quatationParticularsAmount);
//
//        return saveQuatationParticularAmount;
//
//    }






}
