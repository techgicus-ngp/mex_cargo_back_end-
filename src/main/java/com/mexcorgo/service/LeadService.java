package com.mexcorgo.service;


import com.mexcorgo.component.FollowUpStatus;
import com.mexcorgo.component.LeadActionType;
import com.mexcorgo.component.LeadStage;
import com.mexcorgo.component.LeadStatus;
import com.mexcorgo.datamodel.*;
import com.mexcorgo.dto.FollowUpDto.FollowUpBasicResponse;
import com.mexcorgo.dto.FollowUpDto.FollowUpRequest;
import com.mexcorgo.dto.request.*;
import com.mexcorgo.dto.response.*;
import com.mexcorgo.exception.*;
import com.mexcorgo.mapper.*;
import com.mexcorgo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeadService {

           private LeadRepository leadRepository;
           private CompanyRepository companyRepository;
           private CompanyEmployeeRepository companyEmployeeRepository;
           private NeedRepository needRepository;
         //  private NoteRepository noteRepository;
           private AdditionalNeedRepository additionalNeedRepository;
           private FollowUpRepository followUpRepository;
           private NeedExtraDetailsRepository needExtraDetailsRepository;
           private CompanyResponseMapper companyResponseMapper;
           private CompanyEmployeeResponseMapper companyEmployeeResponseMapper;
           private NeedResponseMapper needResponseMapper;
           private CompanyMapper companyMapper;
           private CompanyEmployeeMapper companyEmployeeMapper;
           private NeedMapper needMapper;
           private NeedExtraDetailsMapper needExtraDetailsMapper;
           private SalesAndMarketingTaskRepository salesAndMarketingTaskRepository;
           private EndUserDetailsRepository endUserDetailsRepository;
           private QuatationRepository quatationRepository;
           private QuatationParticularsAmountRepository quatationParticularsAmountRepository;
           private LeadTimelineService leadTimelineService;

           Logger logger = LoggerFactory.getLogger(LeadService.class);

    @Autowired
    public LeadService(LeadRepository leadRepository, CompanyRepository companyRepository, CompanyEmployeeRepository companyEmployeeRepository, NeedRepository needRepository, AdditionalNeedRepository additionalNeedRepository, FollowUpRepository followUpRepository, NeedExtraDetailsRepository needExtraDetailsRepository, CompanyResponseMapper companyResponseMapper, CompanyEmployeeResponseMapper companyEmployeeResponseMapper, NeedResponseMapper needResponseMapper, CompanyMapper companyMapper, CompanyEmployeeMapper companyEmployeeMapper, NeedMapper needMapper, NeedExtraDetailsMapper needExtraDetailsMapper, SalesAndMarketingTaskRepository salesAndMarketingTaskRepository, EndUserDetailsRepository endUserDetailsRepository, QuatationRepository quatationRepository, QuatationParticularsAmountRepository quatationParticularsAmountRepository, LeadTimelineService leadTimelineService) {
        this.leadRepository = leadRepository;
        this.companyRepository = companyRepository;
        this.companyEmployeeRepository = companyEmployeeRepository;
        this.needRepository = needRepository;
        this.additionalNeedRepository = additionalNeedRepository;
        this.followUpRepository = followUpRepository;
        this.needExtraDetailsRepository = needExtraDetailsRepository;
        this.companyResponseMapper = companyResponseMapper;
        this.companyEmployeeResponseMapper = companyEmployeeResponseMapper;
        this.needResponseMapper = needResponseMapper;
        this.companyMapper = companyMapper;
        this.companyEmployeeMapper = companyEmployeeMapper;
        this.needMapper = needMapper;
        this.needExtraDetailsMapper = needExtraDetailsMapper;
        this.salesAndMarketingTaskRepository = salesAndMarketingTaskRepository;
        this.endUserDetailsRepository = endUserDetailsRepository;
        this.quatationRepository = quatationRepository;
        this.quatationParticularsAmountRepository = quatationParticularsAmountRepository;
        this.leadTimelineService = leadTimelineService;
    }

    @Transactional
    public Lead createLead(CreateLeadRequest createLeadRequest,User currentUser){

        Lead createNewLead = new Lead();
        createNewLead.setUser(currentUser);
        createNewLead.setLeadReferenceNo(generateLeadReferenceNumber(currentUser.getUserId()));
        createNewLead = convertCreatedLeadRequestIntoLeed(createLeadRequest,createNewLead);
        createNewLead.setDataReferenceNo(generateDataReferenceNo(currentUser.getUserId()));

        ZonedDateTime istDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

        createNewLead.setLeadCreatedDate(istDateTime.toLocalDate());
        createNewLead.setLeadCreatedTime(istDateTime.toLocalTime());

        logger.info("lead is set with the user");

        Company newCompany = new Company();
         newCompany = convertCreateLeadRequestIntoCompany(createLeadRequest,newCompany);

         // In the new lead we set the company
        createNewLead.setCompany(newCompany);

        logger.info("company is set in the lead");

        CompanyEmployee newCompanyEmployee = new CompanyEmployee();
        newCompanyEmployee = convertCreatedLeadRequestIntoCompanyEmployee(createLeadRequest,newCompanyEmployee);

        // In the new lead we set the company employee
        createNewLead.setCompanyEmployee(newCompanyEmployee);

        logger.info("company employee is set in the lead");


        EndUserDetails newEndUserDetails = new EndUserDetails();
        newEndUserDetails = convertCreatedLeadRequestIntoEndUserDetails(createLeadRequest,newEndUserDetails);

        //In the new lead we set end user details
        createNewLead.setEndUserDetails(newEndUserDetails);

        logger.info("End user is set in the lead");

        Need newNeed = new Need();
         newNeed = converCreatedLeadIntoNeed(createLeadRequest,newNeed);

         createNewLead.setNeed(newNeed);

        logger.info("need is set in the lead");

//        Note newNote = new Note();
//        newNote = convertCreateLeadRequestIntoNote(createLeadRequest,newNote,currentUser);

        FollowUpRequest followUpRequest = createLeadRequest.getFollowUpRequest();

         FollowUp followUp = new FollowUp();
         followUp = convertFollowUpRequestIntoFollowUp(followUpRequest,currentUser,createNewLead);

//        createNewLead.setNote(newNote);

        List<FollowUp> followUpList = new ArrayList<>();
        followUpList.add(followUp);

        createNewLead.setFollowUpList(followUpList);

        createNewLead.setFollowUpStatus(followUpRequest.getFollowUpStatus());

        logger.info("followUp added in the list.");

        newCompany.setLead(createNewLead);
        newCompanyEmployee.setLead(createNewLead);
        newEndUserDetails.setLead(createNewLead);
        newNeed.setLead(createNewLead);
       // newNote.setLead(createNewLead);


        // save lead stages and status
       // createNewLead.setCurrentStage(LeadStage.SALES);
        createNewLead.setCurrentStatus(LeadStatus.CREATED);

      Lead saveLead = leadRepository.save(createNewLead);

        logger.info("lead is save in the lead");

        ZoneId zoneId = ZoneId.of("Asia/Kolkata");

        ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(zoneId);

        LeadTimeline leadTimeline = leadTimelineService.setLeadTimeLineFieldsAndSave(
                saveLead,
                null,
//                LeadStage.SALES,
//                LeadStatus.CREATED,
//                "Lead created by Sales",
                saveLead.getUser(),
               zonedDateTime,
                LeadActionType.CREATED
        );



       return saveLead;
    }




    public Page<LeadResponseDto> getUserLeads(Long userId, int page, int size){

        //created Pageable object with page number and page size
        Pageable pageable = PageRequest.of(page,size,Sort.by(Sort.Order.desc("leadDate"),Sort.Order.desc("leadId")));

      //  logger.info("Pageable is converting");
        // Log page and size for debugging
        logger.info("Fetching leads for userId: {}, page: {}, size: {}", userId, pageable.getPageNumber(), pageable.getPageSize());

        Page<LeadResponseInterface> leadPage = leadRepository.findByUser_UserIdOrderByLeadDateDesc(userId,pageable);

        // Log the number of records fetched
        logger.info("Fetched {} leads", leadPage.getContent().size());

//        leadPage.forEach(lead -> logger.info("Lead ID: {}", lead.getLeadId()));
//        logger.error("I got error here");

        Page<LeadResponseDto> leadResponseDtos = leadPage.map(lead-> {
            LeadResponseDto leadResponseDto = new LeadResponseDto();
            leadResponseDto.setLeadId(lead.getLeadId());
            leadResponseDto.setLeadReferenceNo(lead.getLeadReferenceNo());
            leadResponseDto.setLeadDate(lead.getLeadDate());
            leadResponseDto.setLeadTime(lead.getLeadTime());
            leadResponseDto.setWayOfLead(lead.getWayOfLead());
            leadResponseDto.setModeOfCommunication(lead.getModeOfCommunication());
            leadResponseDto.setDataReference(lead.getDataReference());
            leadResponseDto.setIsQuatationCreated(lead.getIsQuatationCreated());
            leadResponseDto.setIsQuatationSendToUser(lead.getIsQuatationSendToUser());
            leadResponseDto.setFollowUpStatus(lead.getFollowUpStatus());
            leadResponseDto.setIsVerbalConfirmationTaskCompleted(lead.getIsVerbalConfirmationTaskCompleted());
            leadResponseDto.setDataReferenceNo(lead.getDataReferenceNo());
            leadResponseDto.setDepartmentName(lead.getDepartmentName());
            leadResponseDto.setLeadCreatedDate(lead.getLeadCreatedDate());
            leadResponseDto.setLeadCreatedTime(lead.getLeadCreatedTime());

            // Log the ID of each lead being mapped
            logger.info("Mapped Lead ID: {}", lead.getLeadId());

            return leadResponseDto;
        });

        return leadResponseDtos;
    }


    public Company getCompanyDetailsForLead(Long leadId){
        return companyRepository.findByLead_LeadId(leadId);
    }


    public CompanyEmployee getCompanyEmployeeDetailsForLead(Long leadId){
        return companyEmployeeRepository.findByLead_LeadId(leadId);
    }


    public Need getNeedForLead(Long leadId){
        return needRepository.findByLead_LeadId(leadId);
    }

//    public Note getNoteForLead(Long leadId){
//        return  noteRepository.findByLead_LeadId(leadId);
//    }

    public Boolean deleteTheLead(Long leadId){
        Lead existingLead = leadRepository.findById(leadId)
                .orElseThrow(()->new UserNotFoundException("Lead is not found with id : "+leadId));

        leadRepository.delete(existingLead);

        return true;
    }

    public LeadResponseDto getLeadById(Long leadId){
      LeadResponseInterface leadResponseInterface = leadRepository.findByLeadId(leadId);
      LeadResponseDto leadResponseDto = new LeadResponseDto();
      leadResponseDto.setLeadId(leadResponseInterface.getLeadId());
      leadResponseDto.setLeadReferenceNo(leadResponseInterface.getLeadReferenceNo());
      leadResponseDto.setLeadDate(leadResponseInterface.getLeadDate());
      leadResponseDto.setLeadTime(leadResponseInterface.getLeadTime());
      leadResponseDto.setWayOfLead(leadResponseInterface.getWayOfLead());
      leadResponseDto.setModeOfCommunication(leadResponseInterface.getModeOfCommunication());
      leadResponseDto.setDataReference(leadResponseInterface.getDataReference());
      leadResponseDto.setDataReferenceNo(leadResponseInterface.getDataReferenceNo());
      leadResponseDto.setDepartmentName(leadResponseInterface.getDepartmentName());
      leadResponseDto.setLeadCreatedDate(leadResponseInterface.getLeadCreatedDate());
      leadResponseDto.setLeadCreatedTime(leadResponseInterface.getLeadCreatedTime());
      return leadResponseDto;
    }


    @Transactional
    public LeadResponseDto updateLeadDetails(Long leadId, LeadRequestDto leadRequestDto){
        Lead existingLead = leadRepository.findById(leadId)
                .orElseThrow(()-> new UserNotFoundException("Lead is not found with id : "+leadId));
        existingLead.setLeadTime(leadRequestDto.getLeadTime());
        existingLead.setLeadDate(leadRequestDto.getLeadDate());
        existingLead.setLeadReferenceNo(leadRequestDto.getLeadReferenceNo());
        existingLead.setWayOfLead(leadRequestDto.getWayOfLead());
        existingLead.setModeOfCommunication(leadRequestDto.getModeOfCommunication());
        existingLead.setDataReference(leadRequestDto.getDataReference());

        Lead saveLead = leadRepository.save(existingLead);

        LeadResponseDto leadResponseDto = convertLeadIntoLeadResponseDto(saveLead,new LeadResponseDto());

        return leadResponseDto;
    }


    public NeedExtraDetails afterLeadConfirmAddExtraNeedData(Long leadId,NeedExtraDetailsRequestDto needExtraDetailsRequestDto){
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


    public Company updateCompanyDetails(Long leadId, CompanyRequest companyRequest){
     Company existingCompany = companyRepository.findByLead_LeadId(leadId);
         existingCompany.setCompanyName(companyRequest.getCompanyName());
         existingCompany.setCompanySector(companyRequest.getCompanySector());
         existingCompany.setCompanySetUp(companyRequest.getCompanySetUp());
         existingCompany.setState(companyRequest.getState());
         existingCompany.setHeadOfOffice(companyRequest.getHeadOfOffice());
         existingCompany.setEasyHubCentre(companyRequest.getEasyHubCentre());
         existingCompany.setMajorHub(companyRequest.getMajorHub());
         existingCompany.setMinorHub(companyRequest.getMinorHub());

       Company saveCompany = companyRepository.save(existingCompany);

       return saveCompany;
    }

    public CompanyEmployee updateCompanyEmployeeDetails(Long leadId,CompanyEmplyeeRequest companyEmplyeeRequest){
        CompanyEmployee existingCompanyEmployee = companyEmployeeRepository.findByLead_LeadId(leadId);
        existingCompanyEmployee.setEmployeeName(companyEmplyeeRequest.getEmployeeName());
        existingCompanyEmployee.setDesignation(companyEmplyeeRequest.getDesignation());
        existingCompanyEmployee.setMailId(companyEmplyeeRequest.getMailId());
        existingCompanyEmployee.setDepartment(companyEmplyeeRequest.getDepartment());
        existingCompanyEmployee.setContactNo(companyEmplyeeRequest.getContactNo());
        existingCompanyEmployee.setLandLineNo(companyEmplyeeRequest.getLandLineNo());

        CompanyEmployee saveCompanyEmployee = companyEmployeeRepository.save(existingCompanyEmployee);

        return saveCompanyEmployee;
    }


    public Need updateNeedDetails(Long leadId,NeedRequest needRequest){
        Need existingNeed = needRepository.findByLead_LeadId(leadId);

        existingNeed.setSource(needRequest.getSource());
        existingNeed.setDestination(needRequest.getDestination());
        existingNeed.setMovingDateAndTime(needRequest.getMovingDateAndTime());
        existingNeed.setReceivingDateAndTime(needRequest.getReceivingDateAndTime());
        existingNeed.setWeight(needRequest.getWeight());
        existingNeed.setCommodity(needRequest.getCommodity());
        existingNeed.setCommodityValue(needRequest.getCommodityValue());
        existingNeed.setSize(needRequest.getSize());
       // existingNeed.setInsuranceFacilityOfGoods(needRequest.getInsuranceFacilityOfGoods());
        existingNeed.setGoodTransport(needRequest.getGoodTransport());
        existingNeed.setWhenWeGetGoods(needRequest.getWhenWeGetGoods());
        existingNeed.setVehicleValue(needRequest.getVehicleValue());
        existingNeed.setTypeOfTransporatation(needRequest.getTypeOfTransporatation());
        existingNeed.setSizeOfTransporatation(needRequest.getSizeOfTransporatation());
        existingNeed.setCarTransport(needRequest.getCarTransport());
        existingNeed.setAnyThingsElseRatherThanGood(needRequest.getAnyThingsElseRatherThanGood());
        existingNeed.setAnyWareHouseFacilityRatherThanThisThings(needRequest.getAnyWareHouseFacilityRatherThanThisThings());
      //  existingNeed.setCommudityAndOtherGoodsInsuranceFacility(needRequest.getCommudityAndOtherGoodsInsuranceFacility());
         existingNeed.setOtherServices(needRequest.getOtherServices());
         existingNeed.setRiskCoverageGood(needRequest.getRiskCoverageGood());

        // Handling AdditionalNeed updates
        List<AdditionalNeed> updatedAdditionalNeeds = needRequest.getAdditionalNeedRequestList().stream().map(req -> {
            AdditionalNeed additionalNeed = existingNeed.getAdditionalNeed().stream()
                    .filter(an -> an.getAdditionalNeedId().equals(req.getAdditionalNeedId()))
                    .findFirst()
                    .orElse(new AdditionalNeed()); // If not found, create new

            additionalNeed.setNeed(existingNeed);
            additionalNeed.setArticleName(req.getNeedName());
            additionalNeed.setArticleValue(req.getNeedValue());

            return additionalNeed;
        }).collect(Collectors.toList());

        // Remove AdditionalNeeds that are not in the updated list
        existingNeed.getAdditionalNeed().removeIf(existing ->
                updatedAdditionalNeeds.stream().noneMatch(updated -> updated.getAdditionalNeedId().equals(existing.getAdditionalNeedId()))
        );

        existingNeed.getAdditionalNeed().clear();
        existingNeed.getAdditionalNeed().addAll(updatedAdditionalNeeds);

        Need saveNeed = needRepository.save(existingNeed);

        return saveNeed;

    }


//    public Note updateNote(Long leadId,NoteRequest noteRequest){
//        Note existingNote = noteRepository.findByLead_LeadId(leadId);
//
//        // Update Note fields
//        existingNote.setRemark(noteRequest.getRemark());
//        existingNote.setDate(noteRequest.getDate());
//        existingNote.setTime(noteRequest.getTime());
//        existingNote.setRating(noteRequest.getRating());
//
//        // Update FollowUps
//        List<FollowUp> updatedFollowUps = noteRequest.getFollowUps().stream().map(req -> {
//            FollowUp followUp;
//            if (req.getFollowUpId() != null) {
//                // Update existing follow-up
//                followUp = followUpRepository.findById(req.getFollowUpId())
//                        .orElseThrow(() -> new RuntimeException("FollowUp not found"));
//            } else {
//                // Create new follow-up
//                followUp = new FollowUp();
//                followUp.setNote(existingNote);
//            }
//
//            followUp.setFollowUpDate(req.getFollowUpDate());
//            followUp.setFollowUpRemark(req.getFollowUpRemark());
//            return followUp;
//        }).collect(Collectors.toList());
//
//        existingNote.getFollowUps().clear();
//        existingNote.getFollowUps().addAll(updatedFollowUps);
//
//        return noteRepository.save(existingNote);
//    }

    private LeadResponseDto convertLeadIntoLeadResponseDto(Lead lead,LeadResponseDto leadResponseDto){
        leadResponseDto.setLeadId(lead.getLeadId());
        leadResponseDto.setLeadReferenceNo(lead.getLeadReferenceNo());
        leadResponseDto.setLeadDate(lead.getLeadDate());
        leadResponseDto.setLeadTime(lead.getLeadTime());
        leadResponseDto.setWayOfLead(lead.getWayOfLead());
        leadResponseDto.setModeOfCommunication(lead.getModeOfCommunication());
        leadResponseDto.setDataReference(lead.getDataReference());
        return leadResponseDto;
    }


    @Transactional
    public FollowUpBasicResponse addFollowUp(Long leadId,FollowUpRequest followUpRequest){

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(()-> new ResourceNotFoundException("Lead not found with id : "+leadId));

        if (lead.getFollowUpStatus().equals(FollowUpStatus.COMPLETED)){
            throw new LeadAlreadyCompletedException("Lead already completed, so you can not add the followUp.");
        }

        LocalDate followUpDate = followUpRequest.getFollowUpDate();
        LocalDate nextFolloUpDate = followUpRequest.getNextFollowUpDate();

        if (followUpDate.isBefore(lead.getLeadDate())){
            throw new IllegalArgumentException("FollowUp date is cannot be earlier than the lead date." +
                    "The lead date is "+lead.getLeadDate()+" and followUp date is : "+followUpDate);
        }

        if (followUpRequest.getFollowUpStatus().equals(FollowUpStatus.COMPLETED)){

            // Check: latest follow-up logic
            LocalDate latestFollowUpDate = followUpRepository
                    .findMaxFollowUpDateByLead(lead);

            // If latest date exists and request date is before it → invalid
            if (latestFollowUpDate != null && followUpDate.isBefore(latestFollowUpDate)) {
                throw new IllegalArgumentException("The most recent follow-up happened on "+
                        latestFollowUpDate + ", but you are trying to mark the lead as completed on " +
                        followUpDate + ", which is earlier date.");
            }

            lead.setLeadCompletedDate(followUpDate);

        }

        if (nextFolloUpDate !=null){
            if (nextFolloUpDate.isBefore(followUpDate)){
                throw new IllegalArgumentException("Next follow Up date cannot be earlier than the followUp date." +
                        "Next followUp date : "+nextFolloUpDate+", followUp date : "+followUpDate);
            }
        }

        lead.setFollowUpStatus(followUpRequest.getFollowUpStatus());

        leadRepository.save(lead);

        FollowUp  followUp = convertFollowUpRequestIntoFollowUp(followUpRequest,lead.getUser(),lead);

        followUpRepository.save(followUp);

        FollowUpBasicResponse followUpBasicResponse = new FollowUpBasicResponse();
        followUpBasicResponse = convertFollowUpIntoRespose(followUp);

        return followUpBasicResponse;

    }


    public List<FollowUpBasicResponse> getLeadFollowUpList(Long leadId){
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(()-> new ResourceNotFoundException("Lead not found with id : "+leadId));

        List<FollowUp> followUpList = followUpRepository.findByLead(lead);

        List<FollowUpBasicResponse> followUpBasicResponseList = new ArrayList<>();
        followUpBasicResponseList = convertFollowUpListIntoResponseList(followUpList);

         followUpBasicResponseList.sort(Comparator.comparing(FollowUpBasicResponse::getFollowUpDate).reversed());

         return followUpBasicResponseList;

    }

//    @Transactional
//    public Note addFollowUp(Long leadId, FollowUp followUp, FollowUpStatus followUpStatus,User currentUser) {
//
//        Note note = noteRepository.findByLead_LeadId(leadId);
//
//        note.setFollowUpStatus(followUpStatus);
//
//        followUp.setNote(note);
//        followUp.setUser(currentUser);
//        followUpRepository.save(followUp);
//
//        note.getFollowUps().add(followUp);
//
//
//        Lead lead = leadRepository.findByNote_NoteId(note.getNoteId());
//
//        Note saveNote = noteRepository.save(note);
//
//        if (followUpStatus == FollowUpStatus.COMPLETED){
//            SalesAndMarketingTasks task1 = new SalesAndMarketingTasks("Verbal Confirmation", "Tick after verbal confirmation", lead);
//            SalesAndMarketingTasks task2 = new SalesAndMarketingTasks("Get Client Work Order", "Get work order date & time", lead);
//            SalesAndMarketingTasks task3 = new SalesAndMarketingTasks("Agreement Paper Sent", "Add agreement number and send date & time", lead);
//            task3.setAgreementNumber(""); // to be filled later
//
//            SalesAndMarketingTasks task4 = new SalesAndMarketingTasks("Agreement Confirmation", "Add confirmation mode and agreement number", lead);
//            task4.setAgreementNumber("");
//            task4.setConfirmationMode("");
//
//            salesAndMarketingTaskRepository.saveAll(List.of(task1, task2, task3, task4));
//        }
//
//        return saveNote;
//    }

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

    private Company convertCreateLeadRequestIntoCompany(CreateLeadRequest createLeadRequest,Company newCompany){
        newCompany.setCompanyName(createLeadRequest.getCompanyName());
        newCompany.setHeadOfOffice(createLeadRequest.getCompanyHeadOffice());
        newCompany.setState(createLeadRequest.getState());
        newCompany.setCompanySetUp(createLeadRequest.getCompanySetUp());
        newCompany.setCompanySector(createLeadRequest.getCompanySector());
        newCompany.setEasyHubCentre(createLeadRequest.getEasyHubCentre());
        newCompany.setMinorHub(createLeadRequest.getMinorHub());
        newCompany.setMajorHub(createLeadRequest.getMajorHub());
        return newCompany;
    }


    private CompanyEmployee convertCreatedLeadRequestIntoCompanyEmployee(CreateLeadRequest createLeadRequest,
                                                                         CompanyEmployee newCompanyEmployee){
        newCompanyEmployee.setEmployeeName(createLeadRequest.getEmployeeName());
        newCompanyEmployee.setDepartment(createLeadRequest.getDepartment());
        newCompanyEmployee.setDesignation(createLeadRequest.getDesignation());
        newCompanyEmployee.setContactNo(createLeadRequest.getContactNo());
        newCompanyEmployee.setMailId(createLeadRequest.getMailId());
        newCompanyEmployee.setLandLineNo(createLeadRequest.getLandLineNo());

        return newCompanyEmployee;
    }

    private Need converCreatedLeadIntoNeed(CreateLeadRequest createLeadRequest,Need newNeed){
        newNeed.setSource(createLeadRequest.getSource());
        newNeed.setDestination(createLeadRequest.getDestination());
        newNeed.setOriginFloorNo(createLeadRequest.getOriginFloorNo());
        newNeed.setDestinationFloorNo(createLeadRequest.getDestinationFloorNo());
        newNeed.setOriginDetailsAddress(createLeadRequest.getOriginDetailsAddress());
        newNeed.setDestinationDetailsAddress(createLeadRequest.getDestinationDetailsAddress());
        newNeed.setIsLiftAvailableInOrigin(createLeadRequest.getIsLiftAvailableInOrigin());
        newNeed.setIsLiftAvailableInDestination(createLeadRequest.getIsLiftAvailableInDestination());
        newNeed.setSpecialService(createLeadRequest.getSpecialService());

        if (createLeadRequest.getMovingDateAndTime() == null){
            newNeed.setMovingDateAndTime(null);
        }
        else {
            newNeed.setMovingDateAndTime(LocalDateTime.parse(createLeadRequest.getMovingDateAndTime()));
        }
        if (createLeadRequest.getReceivingDateTime() == null){
            newNeed.setReceivingDateAndTime(null);
        }
        else {
            newNeed.setReceivingDateAndTime(LocalDateTime.parse(createLeadRequest.getReceivingDateTime()));
        }
        newNeed.setCommodity(createLeadRequest.getCommodity());
        newNeed.setSize(createLeadRequest.getSize());
        newNeed.setWeight(createLeadRequest.getWeight());
        newNeed.setSecondWeightValue(createLeadRequest.getSecondWeightValue());
        newNeed.setOverAllWeightValue(createLeadRequest.getOverAllWeightValue());
        newNeed.setTypeOfTransporatation(createLeadRequest.getTypeOfTransportation());
        newNeed.setSizeOfTransporatation(createLeadRequest.getSizeOfTransportation());
        newNeed.setPreferredRoot(createLeadRequest.getPreferredRoot());
        newNeed.setCommodityValue(createLeadRequest.getCommodityValue());
        newNeed.setVehicleValue(createLeadRequest.getVehicleValue());
        newNeed.setGoodTransport(createLeadRequest.getGoodsTransport());
        newNeed.setCarTransport(createLeadRequest.getCarTransport());

        if (createLeadRequest.getCarMovingDate() == null){
            newNeed.setCarMovingDate(null);
        }
        else {
            newNeed.setCarMovingDate(createLeadRequest.getCarMovingDate());
        }
        if (createLeadRequest.getCarMovingTime() == null){
            newNeed.setCarMovingTime(null);
        }
        else {
            newNeed.setCarMovingTime(createLeadRequest.getCarMovingTime());
        }

        if (createLeadRequest.getCarReceivingDate() == null){
            newNeed.setCarReceivingDate(null);
        }
        else {
            newNeed.setCarReceivingDate(createLeadRequest.getCarReceivingDate());
        }
        if (createLeadRequest.getCarReceivingTime()==null) {
            newNeed.setCarReceivingTime(null);
        }
        else{
            newNeed.setCarReceivingTime(createLeadRequest.getCarReceivingTime());
        }
        newNeed.setWhenWeGetGoods(createLeadRequest.getWhenWeGetGoods());
        newNeed.setAnyThingsElseRatherThanGood(createLeadRequest.getAnyThingElseRatherThanGood());
        newNeed.setAnyWareHouseFacilityRatherThanThisThings(createLeadRequest.getAnyWarehouseFacilityRatherThanThisThings());
       // newNeed.setInsuranceFacilityOfGoods(createLeadRequest.getInsuranceFacilityOfGoods());
       // newNeed.setCommudityAndOtherGoodsInsuranceFacility(createLeadRequest.getCommodityAndOtherGoodsInsuranceFacility());
        newNeed.setRiskCoverageGood(createLeadRequest.getRiskCoverageGood());
        newNeed.setOtherServices(createLeadRequest.getOtherServices());

        List<AdditionalNeed> newAdditionalNeed = new ArrayList<>();

        if (createLeadRequest.getAdditionalNeedRequests() !=null) {
            newNeed.setAdditionalNeed(convertAdditionalNeedRequesrtIntoAdditinalNeedList
                    (createLeadRequest.getAdditionalNeedRequests(), newAdditionalNeed, newNeed));
        }

        return newNeed;

    }



    private List<AdditionalNeed> convertAdditionalNeedRequesrtIntoAdditinalNeedList(List<AdditionalNeedRequest> additionalNeedRequestList,
                                                                     List<AdditionalNeed> additionalNeedList,Need newNeed){
        additionalNeedList = additionalNeedRequestList.stream()
                .map(additionalNeed->{
                    AdditionalNeed newAdditionalNeed = new AdditionalNeed();
                    newAdditionalNeed.setArticleName(additionalNeed.getArticleName());
                    newAdditionalNeed.setArticleValue(additionalNeed.getArticleValue());
                    newAdditionalNeed.setArticleDimension(additionalNeed.getArticleDimension());
                    newAdditionalNeed.setArticleWeight(additionalNeed.getArticleWeight());
                    newAdditionalNeed.setNeed(newNeed);

                    return newAdditionalNeed;
                }).toList();

        return additionalNeedList;
    }


    private Lead convertCreatedLeadRequestIntoLeed(CreateLeadRequest createLeadRequest,Lead createNewLead){
        if(createLeadRequest.getDate() == null){
            createNewLead.setLeadDate(null);
        }else {
            createNewLead.setLeadDate(createLeadRequest.getDate());
        }
        if (createLeadRequest.getTime() == null){
            createNewLead.setLeadTime(null);
        }
        {
            createNewLead.setLeadTime(createLeadRequest.getTime());
        }
        createNewLead.setWayOfLead(createLeadRequest.getWayOfLead());
        createNewLead.setModeOfCommunication(createLeadRequest.getModeOfCommunication());
        createNewLead.setDataReference(createLeadRequest.getDataReference());

        return createNewLead;
    }


    private FollowUp convertFollowUpRequestIntoFollowUp(FollowUpRequest request,User user,Lead lead){
        FollowUp followUp = new FollowUp();
        followUp.setFollowUpDate(request.getFollowUpDate());
        followUp.setFollowUpTime(request.getFollowUpTime());
        followUp.setRating(request.getRating());
        followUp.setOutcome(request.getOutcome());
        followUp.setNextFollowUpDate(request.getNextFollowUpDate());
        followUp.setNextFollowUpTime(request.getNextFollowUpTime());
        followUp.setFollowUpStatus(request.getFollowUpStatus());
        followUp.setRemark(request.getRemark());
        followUp.setUser(user);
        followUp.setLead(lead);

        return followUp;
    }


    public List<FollowUpBasicResponse> convertFollowUpListIntoResponseList(List<FollowUp> followUpList){
        List<FollowUpBasicResponse> responseList = followUpList.stream()
                .map(this:: convertFollowUpIntoRespose)
                .collect(Collectors.toList());
                //.toList(); // using this we make or list mutable

        return responseList;
    }

//    private Note convertCreateLeadRequestIntoNote(CreateLeadRequest createLeadRequest,Note newNote,User currentUser){
//        newNote.setRemark(createLeadRequest.getRemark());
//        if (createLeadRequest.getRemarkDate() == null){
//            newNote.setDate(null);
//        }else {
//            newNote.setDate(LocalDate.parse(createLeadRequest.getRemarkDate()));
//        }
//
//        if (createLeadRequest.getRemarkTime() == null){
//            newNote.setTime(null);
//        }
//        else {
//            newNote.setTime(LocalTime.parse(createLeadRequest.getRemarkTime()));
//        }
//        newNote.setRating(createLeadRequest.getRating());
//        newNote.setFollowUpStatus(createLeadRequest.getFollowUpStatus());
//
//        FollowUp newFollowUp = new FollowUp();
//        if (createLeadRequest.getFollowUpRequest() !=null) {
//            newNote.setFollowUps(List.of(convertFollowUpRequestIntoFollowUp(createLeadRequest.getFollowUpRequest(), newFollowUp, currentUser, newNote)));
//        }
//
//        return newNote;
//    }


//    private FollowUp convertFollowUpRequestIntoFollowUp(FollowUpRequest followUpRequest, FollowUp newFollowUp, User currentUser, Note newNote){
//
//        if (followUpRequest.getFollowUpDate() == null) {
//            newFollowUp.setFollowUpDate(null);
//        }
//        else {
//            newFollowUp.setFollowUpDate(LocalDate.parse(followUpRequest.getFollowUpDate()));
//        }
//        newFollowUp.setFollowUpRemark(followUpRequest.getFollowUpRemark());
//        newFollowUp.setUser(currentUser);
//        newFollowUp.setNote(newNote);
//        return newFollowUp;
//    }

    private EndUserDetails convertCreatedLeadRequestIntoEndUserDetails(CreateLeadRequest createLeadRequest,
                                            EndUserDetails endUserDetails){

        endUserDetails.setUserName(createLeadRequest.getEndUserName());
        endUserDetails.setDepartment(createLeadRequest.getEndUserDepartment());
        endUserDetails.setDesignation(createLeadRequest.getDesignation());
        endUserDetails.setContactNo(createLeadRequest.getEndUserContactNo());
        endUserDetails.setLandLineNo(createLeadRequest.getEndUserLandLineNo());
        endUserDetails.setMailId(createLeadRequest.getEndUserMailId());

        return endUserDetails;
    }


    @Transactional
    public GetLeadFullDetails getFullLeadDetails(Long leadId){

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(()-> new ResourceNotFoundException("Lead with id : "+leadId+" is not found"));

        GetLeadFullDetails getLeadFullDetails = new GetLeadFullDetails();

        LeadResponseDto leadResponseDto = new LeadResponseDto();
        leadResponseDto.setLeadId(lead.getLeadId());
        leadResponseDto.setLeadReferenceNo(lead.getLeadReferenceNo());
        leadResponseDto.setLeadDate(lead.getLeadDate());
        leadResponseDto.setLeadTime(lead.getLeadTime());
        leadResponseDto.setModeOfCommunication(lead.getModeOfCommunication());
        leadResponseDto.setWayOfLead(lead.getWayOfLead());
        leadResponseDto.setDataReference(lead.getDataReference());

        getLeadFullDetails.setLead(leadResponseDto);

        CompanyResponseDto companyResponseDto = companyResponseMapper.toCompanyResponseDto(lead.getCompany());
        getLeadFullDetails.setCompany(companyResponseDto);

        CompanyEmployeeResponseDto companyEmployeeResponseDto = companyEmployeeResponseMapper.toCompanyEmployeeResponseDto(lead.getCompanyEmployee());
        getLeadFullDetails.setCompanyEmployee(companyEmployeeResponseDto);

        NeedResponseDto needResponseDto = needResponseMapper.toNeedResponseDto(lead.getNeed());
        getLeadFullDetails.setNeed(needResponseDto);

        return getLeadFullDetails;
    }

    private String generateLeadReferenceNumber(Long userId) {
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


    public GetLeadFullDetails updateFullLeadDetails(Long leadId,GetLeadFullDetails getLeadFullDetails){

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(()-> new ResourceNotFoundException("Lead with id : "+leadId+" is not found"));

        LeadResponseDto leadResponseDto = getLeadFullDetails.getLead();

        lead.setLeadReferenceNo(leadResponseDto.getLeadReferenceNo());
        lead.setLeadDate(leadResponseDto.getLeadDate());
        lead.setLeadTime(leadResponseDto.getLeadTime());
        lead.setWayOfLead(leadResponseDto.getWayOfLead());
        lead.setModeOfCommunication(leadResponseDto.getModeOfCommunication());
        lead.setDataReference(leadResponseDto.getDataReference());

        Company company = companyMapper.convertCompanyReponseDtoIntoCompany(getLeadFullDetails.getCompany());
        lead.setCompany(company);
        company.setLead(lead);

        CompanyEmployee companyEmployee = companyEmployeeMapper.converCompanyEmployeeResponseDtoInoCompanyEmployee(getLeadFullDetails.getCompanyEmployee());
        lead.setCompanyEmployee(companyEmployee);
        companyEmployee.setLead(lead);

        Need need = needMapper.converNeedResponseDtoIntoNeed(getLeadFullDetails.getNeed());
        lead.setNeed(need);
        need.setLead(lead);

       Lead saveLead = leadRepository.save(lead);

       LeadResponseDto saveLeadResponseDto = new LeadResponseDto();
       saveLeadResponseDto.setLeadId(saveLead.getLeadId());
       saveLeadResponseDto.setLeadReferenceNo(saveLead.getLeadReferenceNo());
       saveLeadResponseDto.setLeadDate(saveLead.getLeadDate());
       saveLeadResponseDto.setLeadTime(saveLead.getLeadTime());
       saveLeadResponseDto.setModeOfCommunication(saveLead.getModeOfCommunication());
       saveLeadResponseDto.setWayOfLead(saveLead.getWayOfLead());
       saveLeadResponseDto.setDataReference(saveLead.getDataReference());

       CompanyResponseDto companyResponseDto = companyResponseMapper.toCompanyResponseDto(saveLead.getCompany());
       CompanyEmployeeResponseDto companyEmployeeResponseDto = companyEmployeeResponseMapper.toCompanyEmployeeResponseDto(saveLead.getCompanyEmployee());
       NeedResponseDto needResponseDto = needResponseMapper.toNeedResponseDto(saveLead.getNeed());

       GetLeadFullDetails saveLeadFullDetails = new GetLeadFullDetails();
       saveLeadFullDetails.setLead(saveLeadResponseDto);
       saveLeadFullDetails.setCompany(companyResponseDto);
       saveLeadFullDetails.setCompanyEmployee(companyEmployeeResponseDto);
       saveLeadFullDetails.setNeed(needResponseDto);

       return saveLeadFullDetails;
    }


//    public NeedExtraDetails addNeedExtraDetails(Long leadId,NeedExtraDetailsDto needExtraDetailsDto){
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


    public EndUserDetails getEndUserDetailsByLeadId(Long leadId){
        return endUserDetailsRepository.findByLeadLeadId(leadId);
    }

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


    public QuatationParticularsAmount getParticularAmount(Long leadId){
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(()-> new ResourceNotFoundException("Lead with id : "+leadId+" is not found"));

        Quatation quatation = quatationRepository.findByLead_LeadId(lead.getLeadId());

        QuatationParticularsAmount quatationParticularsAmount = quatationParticularsAmountRepository.findByQuatation_QuatationId(quatation.getQuatationId());

        return quatationParticularsAmount;
    }


    public QuatationParticularsAmount updateParticularAmount(Long leadId,AddParticularsRequestDto particularsRequestDto){

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

    private FollowUpBasicResponse convertFollowUpIntoRespose(FollowUp followUp){
        FollowUpBasicResponse followUpBasicResponse = new FollowUpBasicResponse();
        followUpBasicResponse.setFollowUpId(followUp.getFollowUpId());
        followUpBasicResponse.setFollowUpDate(followUp.getFollowUpDate());
        followUpBasicResponse.setFollowUpTime(followUp.getFollowUpTime());
        followUpBasicResponse.setRating(followUp.getRating());
        followUpBasicResponse.setOutcome(followUp.getOutcome());
        followUpBasicResponse.setNextFollowUpDate(followUp.getNextFollowUpDate());
        followUpBasicResponse.setNextFollowUpTime(followUp.getNextFollowUpTime());
        followUpBasicResponse.setFollowUpStatus(followUp.getFollowUpStatus());
        followUpBasicResponse.setRemark(followUp.getRemark());

        return followUpBasicResponse;

    }


     public Lead setLeadCurrentStageAndCurrentStatus(Lead lead,LeadStage leadStage,LeadStatus status){
        lead.setCurrentStatus(status);
     //   lead.setCurrentStage(leadStage);

        return lead;
     }

}
