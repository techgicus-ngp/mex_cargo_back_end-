package com.mexcorgo.service;


import com.mexcorgo.component.*;
import com.mexcorgo.datamodel.*;
import com.mexcorgo.event.QuotationAssignedEvent;
import com.mexcorgo.dto.request.AddParticularsRequestDto;
import com.mexcorgo.dto.request.AddQuatationWithPurchaseExcutive;
import com.mexcorgo.dto.response.*;
import com.mexcorgo.exception.ResourceNotFoundException;
import com.mexcorgo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalesAndMarketingQuatationService {

           private LeadRepository leadRepository;

           private QuatationRepository quatationRepository;

           private UserRepository userRepository;

           private SendEmailService sendEmailService;

           private NotificationService notificationService;

           private QuatationParticularsAmountRepository quatationParticularsAmountRepository;

           private MasterQuatationResponseRepository masterQuatationResponseRepository;

           private QuatationPlanningExecutiveRepository quatationPlanningExecutiveRepository;

           private QuatationAccountExecutiveRepository quatationAccountExecutiveRepository;
           private LeadTimelineService leadTimelineService;
           private LeadService leadService;
           private LeadStateService leadStateService;
           private final ApplicationEventPublisher eventPublisher;

           Logger logger = LoggerFactory.getLogger(SalesAndMarketingQuatationService.class);

    @Autowired
    public SalesAndMarketingQuatationService(ApplicationEventPublisher eventPublisher, LeadRepository leadRepository, QuatationRepository quatationRepository, UserRepository userRepository, SendEmailService sendEmailService, NotificationService notificationService, QuatationParticularsAmountRepository quatationParticularsAmountRepository, MasterQuatationResponseRepository masterQuatationResponseRepository, QuatationPlanningExecutiveRepository quatationPlanningExecutiveRepository, QuatationAccountExecutiveRepository quatationAccountExecutiveRepository, LeadTimelineService leadTimelineService, LeadService leadService, LeadStateService leadStateService) {
        this.eventPublisher = eventPublisher;
        this.leadRepository = leadRepository;
        this.quatationRepository = quatationRepository;
        this.userRepository = userRepository;
        this.sendEmailService = sendEmailService;
        this.notificationService = notificationService;
        this.quatationParticularsAmountRepository = quatationParticularsAmountRepository;
        this.masterQuatationResponseRepository = masterQuatationResponseRepository;
        this.quatationPlanningExecutiveRepository = quatationPlanningExecutiveRepository;
        this.quatationAccountExecutiveRepository = quatationAccountExecutiveRepository;
        this.leadTimelineService = leadTimelineService;
        this.leadService = leadService;
        this.leadStateService = leadStateService;
    }

    @Transactional
    public QuatationWithPurchaseExcutiveResponse addQuatation(AddQuatationWithPurchaseExcutive addQuatationWithPurchaseExcutive){

       //1. fetch the lead
        Lead lead = fetchLead(addQuatationWithPurchaseExcutive.getLeadId());

        //get purchase excutives
        Set<User> getExistingPurchaseExecutives = fetchExecutives(addQuatationWithPurchaseExcutive.getPurchaseExecutiveIds());

        // 3. Create and save quotation
        Quatation quatation = createAndSaveQuatation(addQuatationWithPurchaseExcutive, lead,getExistingPurchaseExecutives);

        // ✨ Extract values from Need to avoid LazyInitializationException
        Need need = lead.getNeed();
        NeedEmailDetails needEmailDetails = new NeedEmailDetails(
                need.getSource(),
                need.getDestination(),
                need.getCommodity(),
                need.getSize(),
                need.getWeight(),
                need.getTypeOfTransporatation(),
                need.getMovingDateAndTime(),
                need.getOtherServices(),
                need.getCarTransport(),
                need.getCarMovingDate(),
                need.getCarMovingTime()

        );

        //  event trigger only after the quotation store in the database
        // in the event we called notification service which help to send email to
        // multiple user.
        eventPublisher.publishEvent(
                new QuotationAssignedEvent(
                        getExistingPurchaseExecutives,
                        quatation,
                        needEmailDetails
                )
        );


//        // ✅ Send email async
//        notificationService.sendQuotationAssignmentEmailsToPurchaseExcutive(
//                getExistingPurchaseExecutives,
//                quatation,
//                needEmailDetails
//        );

        String purchaseExecutivesNames = getExistingPurchaseExecutives.stream()
                .map(User::getUserName)
                        .collect(Collectors.joining(", "));


        leadStateService.performAction(
                lead,
                LeadEvent.SEND_TO_PURCHASE,
                LeadActionType.SEND_TO_PURCHASE,
                "Quotation sent to Purchase executives: " + purchaseExecutivesNames,
                lead.getUser()
        );

//        // set lead stage and status
//        leadService.setLeadCurrentStageAndCurrentStatus(
//                lead,
//                LeadStage.PURCHASE,
//                LeadStatus.SENT_TO_PURCHASE);
//
//        ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(ZoneId.of("Asia/Kolkata"));
//
//        // set lead time line and save
//        leadTimelineService.setLeadTimeLineFieldsAndSave(
//                lead,
////                 LeadStage.SALES,
////                LeadStage.PURCHASE,
////                LeadStatus.SENT_TO_PURCHASE,
//                "Quatation sent to Purchase executives : "+purchaseExecutivesNames,
//                lead.getUser(),
//                zonedDateTime
//        );

        // 5. Update lead status
        updateLeadQuotationStatus(lead);

        // 6. Return response
        return convertToResponse(quatation);
    }


    private Lead fetchLead(Long leadId) {
        return leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));
    }

    private Set<User> fetchExecutives(Set<Long> ids) {
        Set<User> users = userRepository.findUsersByIds(ids);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No valid purchase executives found");
        }
        return users;
    }

    private Quatation createAndSaveQuatation(AddQuatationWithPurchaseExcutive dto, Lead lead, Set<User> executives) {
        Quatation quatation = convertAddQuationWithPurchaseIntoQuatation(new Quatation(), dto);
        quatation.setLead(lead);
        quatation.setReferredToPurchaseExecutives(executives);
        quatation.setQuatationStatus(QuatationStatus.QUOTATION_CREATED);
        quatation.setIsParticularAmountAdded(false);
        return quatationRepository.save(quatation);
    }

    private void updateLeadQuotationStatus(Lead lead) {
        lead.setIsQuatationCreated(true);
        leadRepository.save(lead);
    }

    private QuatationWithPurchaseExcutiveResponse convertToResponse(Quatation quatation) {
        return convertQuatationIntoQuatationWithPurchaseExcutiveResponse(quatation, new QuatationWithPurchaseExcutiveResponse());
    }


    public List<SalesQuatation> getQuatationRequiredDataToSalesAndMarketingDepartment(Long userId){
         List<SalesQuatation> salesQuatationList = quatationRepository.findQuatationDataByUserId(userId);

         salesQuatationList.sort(Comparator.comparing(SalesQuatation::getQuatationId));

         return salesQuatationList;
     }


     public List<PurchaseEmpResponse> viewPurchaseExecutiveByQuatationId(Long quatationId){
       return quatationRepository.findPurchaseExecutivesByQuatationId(quatationId);
     }


     public List<SalesQuatation> getQuatationWhichPricingIsFinalized(User currentUser){
       return quatationRepository.findQuatationWhichPricingIsFinalized(currentUser.getUserId());
     }





//     public FinalQuatationPricingWithMasterDto viewMasterWithFinalPricing(Long quatationId){
//         FinalQuatationPricingWithMasterDto getfinalQuatationPricingWithMaster = quatationRepository.findFinalQuatationPricingWithMasterByQuatationId(quatationId)
//                 .orElseThrow(()-> new ResourceNotFoundException("Quatation doesn't have final pricing with quatation id is : "+quatationId));
//
//         return getfinalQuatationPricingWithMaster;
//
//     }


//     @Transactional
//     public ApiResponse addQuatationParticularsAmountAndSendToUser(Long quatationId, AddParticularsRequestDto addParticularsRequestDto){
//
//        Quatation existingQuatation = quatationRepository.findById(quatationId)
//                .orElseThrow(()-> new ResourceNotFoundException("Quatation with id : "+quatationId+" is not found"));
//
//         QuatationParticularsAmount quatationParticularsAmount = new QuatationParticularsAmount();
//
//         quatationParticularsAmount.setQuatation(existingQuatation);
//         quatationParticularsAmount.setPackingAmount(addParticularsRequestDto.getPackingAmount());
//         quatationParticularsAmount.setLoadingAmount(addParticularsRequestDto.getLoadingAmount());
//         quatationParticularsAmount.setUnloadingAmount(addParticularsRequestDto.getUnloadingAmount());
//         quatationParticularsAmount.setUnpackingAmount(addParticularsRequestDto.getUnpackingAmount());
//         quatationParticularsAmount.setPackingAndLoadingAmount(addParticularsRequestDto.getPackingAndLoadingAmount());
//         quatationParticularsAmount.setUnloadingAndUnpackingAmount(addParticularsRequestDto.getUnloadingAndUnpackingAmount());
//         quatationParticularsAmount.setPackingAndLoadingAndUnloadingAndUnpackingAmount(addParticularsRequestDto.getPackingAndLoadingAndUnloadingAndUnpackingAmount());
//         quatationParticularsAmount.setTransportationOfHouseholdAmount(addParticularsRequestDto.getTransportationOfHouseholdAmount());
//
//         quatationParticularsAmountRepository.save(quatationParticularsAmount);
//
//        // get end user details from quatation
//         EndUserDetails endUserDetails = existingQuatation.getLead().getEndUserDetails();
//
//         //to avoid lazy intialization error we created new transfer object
//         EmailReceiverInfo emailReceiverInfo = new EmailReceiverInfo(
//               endUserDetails.getMailId(), endUserDetails.getUserName());
//
//         // get need details from quatation
//         Need need = existingQuatation.getLead().getNeed();
//         NeedEmailDetails needEmailDetails = new NeedEmailDetails(
//                 need.getSource(),
//                 need.getDestination(),
//                 need.getCommodity(),
//                 need.getSize(),
//                 need.getWeight(),
//                 need.getTypeOfTransporatation(),
//                 need.getMovingDateAndTime(),
//                 need.getOtherServices(),
//                 need.getCarTransport(),
//                 need.getCarMovingDate(),
//                 need.getCarMovingTime()
//         );
//
//         //send email to end user
//         notificationService.sendQuatationParticularToUser(
//                 emailReceiverInfo,
//                 needEmailDetails,
//                 addParticularsRequestDto
//         );
//
//
//         Map<String,Object> responseData = new HashMap<>();
//         responseData.put("QuationId",quatationId);
//         responseData.put("notifiedUserEmail",emailReceiverInfo.getEmail());
//         responseData.put("Data",addParticularsRequestDto);
//
//         return new ApiResponse(true,"Add Particular amount in Quation",responseData);
//
//     }


       @Transactional
       public QuatationParticularsAmount addParticularAmount(Long quatationId,AddParticularsRequestDto addParticularsRequestDto,
                                                             User currentUser){
           Quatation existingQuatation = quatationRepository.findById(quatationId)
                .orElseThrow(()-> new ResourceNotFoundException("Quatation with id : "+quatationId+" is not found"));

         QuatationParticularsAmount quatationParticularsAmount = new QuatationParticularsAmount();

         quatationParticularsAmount.setQuatation(existingQuatation);
         quatationParticularsAmount.setPackingAmount(addParticularsRequestDto.getPackingAmount());
         quatationParticularsAmount.setLoadingAmount(addParticularsRequestDto.getLoadingAmount());
         quatationParticularsAmount.setUnloadingAmount(addParticularsRequestDto.getUnloadingAmount());
         quatationParticularsAmount.setUnpackingAmount(addParticularsRequestDto.getUnpackingAmount());
         quatationParticularsAmount.setPackingAndLoadingAmount(addParticularsRequestDto.getPackingAndLoadingAmount());
         quatationParticularsAmount.setUnloadingAndUnpackingAmount(addParticularsRequestDto.getUnloadingAndUnpackingAmount());
         quatationParticularsAmount.setPackingAndLoadingAndUnloadingAndUnpackingAmount(addParticularsRequestDto.getPackingAndLoadingAndUnloadingAndUnpackingAmount());
         quatationParticularsAmount.setTransportationOfHouseholdAmount(addParticularsRequestDto.getTransportationOfHouseholdAmount());

         existingQuatation.setIsParticularAmountAdded(true);

         quatationRepository.save(existingQuatation);

           leadStateService.performAction(
                   existingQuatation.getLead(),
                   LeadEvent.PRICING_DONE,
                   LeadActionType.QUOTATION_PRICE_FINALIZED,
                   //  " Master Quotation Response Received ",
                   "Quotation Pricing is finalized.",
                   currentUser
           );

        QuatationParticularsAmount saveQuatationParticularAmount = quatationParticularsAmountRepository.save(quatationParticularsAmount);

        return saveQuatationParticularAmount;

       }


    public QuatationParticularsAmount getParticularAmount(Long quatationId){

       return quatationParticularsAmountRepository.findByQuatation_QuatationId(quatationId);
    }


    public QuatationParticularsAmount updateParticularAmount(Long quatationId,AddParticularsRequestDto particularsRequestDto){

        QuatationParticularsAmount quatationParticularsAmount = quatationParticularsAmountRepository.findByQuatation_QuatationId(quatationId);

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


    @Transactional
    public ApiResponse sendParticularAmountToTheEndUser(Long quatationId){

    Quatation quatation = quatationRepository.findById(quatationId)
                    .orElseThrow(()-> new ResourceNotFoundException("Quatation with id : "+quatationId+" is not found"));


    Lead lead = quatation.getLead();

               // get end user details from quatation
         EndUserDetails endUserDetails = quatation.getLead().getEndUserDetails();

         //to avoid lazy intialization error we created new transfer object
         EmailReceiverInfo emailReceiverInfo = new EmailReceiverInfo(
               endUserDetails.getMailId(), endUserDetails.getUserName());

         // get need details from quatation
         Need need = quatation.getLead().getNeed();
         NeedEmailDetails needEmailDetails = new NeedEmailDetails(
                 need.getSource(),
                 need.getDestination(),
                 need.getCommodity(),
                 need.getSize(),
                 need.getWeight(),
                 need.getTypeOfTransporatation(),
                 need.getMovingDateAndTime(),
                 need.getOtherServices(),
                 need.getCarTransport(),
                 need.getCarMovingDate(),
                 need.getCarMovingTime()
         );

         QuatationParticularsAmount quatationParticularsAmount = quatationParticularsAmountRepository.findByQuatation_QuatationId(quatationId);

         lead.setIsQuatationSendToUser(true);
         leadRepository.save(lead);

         //send email to end user
         notificationService.sendQuatationParticularToUser(
                 emailReceiverInfo,
                 needEmailDetails,
                 quatationParticularsAmount
         );

        leadStateService.performAction(
                lead,
                LeadEvent.SEND_TO_CUSTOMER,
                LeadActionType.SEND_TO_CUSTOMER,
                "Quotation sent to Customer executives: "+lead.getEndUserDetails().getUserName(),
                lead.getUser()
        );

         Map<String,Object> responseData = new HashMap<>();
         responseData.put("QuationId : ",quatationId);
         responseData.put("notifiedUserEmail : ",emailReceiverInfo.getEmail());
         responseData.put("Data",quatationParticularsAmount);

         return new ApiResponse(true,"Particular Amount Send To End User",responseData);

    }

    public List<PlanningEmpResponse> viewPlanningExecutivesByQuatationId(Long quatationId){

      return quatationPlanningExecutiveRepository.findPlanningExecutiveByQuatationId(quatationId);

    }

    public List<AccountEmpResponse> viewAccountExecutivesByQuatationId(Long quatationId){
       return quatationAccountExecutiveRepository.findAccountEmployeesByQutationId(quatationId);
    }

    private Quatation convertAddQuationWithPurchaseIntoQuatation(Quatation newQuatation,AddQuatationWithPurchaseExcutive addQuatationWithPurchaseExcutive){
         newQuatation.setQuatationReferenceNo(addQuatationWithPurchaseExcutive.getQuatationReferenceNo());
         newQuatation.setQuatationRequiredDate(addQuatationWithPurchaseExcutive.getRequiredQuatationDate());
         newQuatation.setQuatationRequiredTime(addQuatationWithPurchaseExcutive.getRequiredQuatationTime());

        ZonedDateTime istDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

         newQuatation.setQuatationForwardedDateToPurchaseExecutive(istDateTime.toLocalDate());
         newQuatation.setQuatationForwardedTimeToPurchaseExecutive(istDateTime.toLocalTime());

         return newQuatation;
     }


     private QuatationWithPurchaseExcutiveResponse convertQuatationIntoQuatationWithPurchaseExcutiveResponse(
             Quatation saveQuatation,QuatationWithPurchaseExcutiveResponse quatationWithPurchaseExcutiveResponse){
         quatationWithPurchaseExcutiveResponse.setQuatationId(saveQuatation.getQuatationId());
         quatationWithPurchaseExcutiveResponse.setQuatationReferenceNo(saveQuatation.getQuatationReferenceNo());
         quatationWithPurchaseExcutiveResponse.setForwardQuatationDate(saveQuatation.getQuatationForwardedDateToPurchaseExecutive());
         quatationWithPurchaseExcutiveResponse.setForwardQuationTime(saveQuatation.getQuatationForwardedTimeToPurchaseExecutive());
         quatationWithPurchaseExcutiveResponse.setRequiredQuatationDate(saveQuatation.getQuatationRequiredDate());
         quatationWithPurchaseExcutiveResponse.setRequiredQuatationTime(saveQuatation.getQuatationRequiredTime());

         return quatationWithPurchaseExcutiveResponse;
     }
}
