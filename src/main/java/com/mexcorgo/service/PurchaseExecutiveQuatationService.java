package com.mexcorgo.service;


import com.mexcorgo.component.*;
import com.mexcorgo.datamodel.*;
import com.mexcorgo.dto.QuatationMasterPricingResponseSqlDto;
import com.mexcorgo.dto.request.MasterQuatationResponseDto;
import com.mexcorgo.dto.request.SendQuatationRequestToMaster;
import com.mexcorgo.dto.request.SendQuatationToPricingExecutiveRequest;
import com.mexcorgo.dto.response.*;
import com.mexcorgo.exception.InvalidLeadStateException;
import com.mexcorgo.exception.ResourceNotFoundException;
import com.mexcorgo.repository.*;
import lombok.RequiredArgsConstructor;
import org.mapstruct.control.MappingControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseExecutiveQuatationService {

    private final QuatationRepository quatationRepository;
    private final LeadRepository leadRepository;
    private final MasterRepository masterRepository;
   // private final SendEmailService sendEmailService;
    private final QuatationMasterRepository quatationMasterRepository;
    private final MasterQuatationResponseRepository masterQuatationResponseRepository;
    private final QuatationPricingExecutiveRepository quatationPricingExecutiveRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
//    private final LeadTimelineService leadTimelineService;
    private final LeadStateService leadStateService;

    public List<PurchaseQuatation> getQuatationFromSalesAndMarketing(Long purchaseExecutiveId){

        List<PurchaseQuatation> getQuatationsBelongToPurchaseExecutive = quatationRepository
                .findQuatationDataByPurchaseExecutiveId(purchaseExecutiveId);

        return getQuatationsBelongToPurchaseExecutive;

    }


    @Transactional
    public ApiResponse sendQuatationToMaster(SendQuatationRequestToMaster sendQuatationRequestToMaster,User currentUser){

        //  Fetch Lead + Need in one query using leadId
        Lead lead = leadRepository.findLeadWithNeedByLeadId(sendQuatationRequestToMaster.getLeadId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));


        // ✅ Fetch related Need details efficiently
        Need need = lead.getNeed();
        if (need == null) {
            throw new ResourceNotFoundException("Lead does not have associated Need details.");
        }

        // ✅ Extract relevant details
//        String origin = need.getSource();
//        String destination = need.getDestination();
//        LocalDate movingDate = need.getMovingDateAndTime().toLocalDate();
//        String commoditySize = need.getSize();
//        String carName = need.getCarTransport();

          NeedEmailDetails needEmailDetails = new NeedEmailDetails(
                  need.getSource(),
                  need.getDestination(),
                  need.getCommodity(),
                  need.getSize(),
                  need.getWeight(),
                  need.getTypeOfTransportation(),
                  need.getMovingDateAndTime(),
                  need.getOtherServices(),
                  need.getCarTransport(),
                  need.getCarMovingDate(),
                  need.getCarMovingTime()
          );


        Quatation quatation = quatationRepository.findByLead_LeadId(lead.getLeadId());

        // ✅ Fetch Masters in one query
        List<Master> masters = masterRepository.findAllById(sendQuatationRequestToMaster.getMasterIds());

        // ✅ Save QuotationMaster entries (Mapping Masters with Lead)
        List<QuatationMaster> quotationMasters = new ArrayList<>();
        for (Master master : masters) {
            QuatationMaster quotationMaster = new QuatationMaster();
            quotationMaster.setMaster(master);

            ZonedDateTime istDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
            quotationMaster.setQuatationForwardDate(istDateTime.toLocalDate());
            quotationMaster.setQuatationForwardTime(istDateTime.toLocalTime());

            quotationMaster.setQuatation(quatation);
            quotationMaster.setSentByPurchaseExecutive(currentUser);
            quotationMasters.add(quotationMaster);
        }
        quatationMasterRepository.saveAll(quotationMasters);

        // ✅ Send Bulk Emails
        List<String> emailAddresses = masters.stream().map(Master::getEmailId).collect(Collectors.toList());
//        String emailBody = buildEmailContentSendToMaster(lead, origin, destination, movingDate, commoditySize, carName);
//        sendEmailService.sendBulkEmail(emailAddresses, "Quotation Request", emailBody);
          notificationService.sendEmailQuatationToMasters(emailAddresses,lead,needEmailDetails);

//        // ✅ Update Lead status
//        lead.setIsQuatationCreated(true);
//        leadRepository.save(lead);


        String masterName = masters.stream()
                .map(Master::getContactName)
                        .collect(Collectors.joining(", "));

        leadStateService.performAction(
                lead,
                LeadEvent.SENT_TO_MASTER,
                LeadActionType.SEND_TO_MASTER,
                "Quatation sent to master : "+masterName,
                currentUser
        );

//        // set leas status
//        lead.setCurrentStatus(LeadStatus.SENT_TO_MASTER);
//        leadRepository.save(lead);
//
//
//        leadTimelineService.setLeadTimeLineFieldsAndSave(
//                lead,
////                LeadStage.PURCHASE,
////                LeadStage.PURCHASE,
////                LeadStatus.SENT_TO_MASTER,
//                "Quatation sent to master : "+masterName,
//                currentUser,
//                ZonedDateTime.now(ZoneId.of("Asia/Kolkata"))
//        );


        // set quatation status to send to quataion service provider
        quatation.setQuatationStatus(QuatationStatus.SENT_TO_SERVICE_PROVIDERS);

        // ✅ Prepare Response Data
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("leadReferenceNo", lead.getLeadReferenceNo());
        responseData.put("mastersNotified", masters.stream().map(Master::getEmailId).collect(Collectors.toList()));

        return new ApiResponse(true,"Quotation request sent successfully",responseData);
    }



    @Transactional
    public MasterQuatationResponse addMasterResponse(MasterQuatationResponseDto dto,User currentUser){
       QuatationMaster quatationMaster = quatationMasterRepository.findById(dto.getQuotationMasterId())
               .orElseThrow(()-> new ResourceNotFoundException("Quatation Master with id : "+dto.getQuotationMasterId()+" is not found"));


          Optional<MasterQuatationResponse> masterQuatationResponse = masterQuatationResponseRepository.findByQuatationMaster(quatationMaster);

          if (masterQuatationResponse.isPresent()){

          MasterQuatationResponse updateMasterResponse =  updateMasterResponseByMasterQuatationResponseId(masterQuatationResponse.get().getMasterQuatationResponseId(),dto,currentUser);

            return updateMasterResponse;
          }

        MasterQuatationResponse response = new MasterQuatationResponse();
        response.setQuatationMaster(quatationMaster);
        response = convertMasterResponseDtoIntoMasterQuatationResponse(response,dto,currentUser);
        response.setResponseAddedBy(currentUser);
        response.setCreatedAt(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));

        Lead lead = quatationMaster.getQuatation().getLead();


        leadStateService.performAction(
                lead,
                LeadEvent.COLLECT_RESPONSE,
                LeadActionType.COLLECT_MASTER_RESPONSE,
              //  " Master Quotation Response Received ",
                "Quotation response received from master: " + quatationMaster.getMaster().getContactName(),
                currentUser
        );

//        // set leas status
//        lead.setCurrentStatus(LeadStatus.MASTER_RESPONSE_RECEIVED);
//        leadRepository.save(lead);
//
//
//        leadTimelineService.setLeadTimeLineFieldsAndSave(
//                lead,
////                LeadStage.PURCHASE,
////                LeadStage.PURCHASE,
////                LeadStatus.MASTER_RESPONSE_RECEIVED,
//                " Master Quotation Response Received : ",
//                currentUser,
//                ZonedDateTime.now(ZoneId.of("Asia/Kolkata"))
//        );

        return masterQuatationResponseRepository.save(response);

    }


    public MasterQuatationResponse updateMasterResponseByMasterQuatationResponseId(Long masterQuatationResponseId,MasterQuatationResponseDto masterQuatationResponseDto,User currentUser){
       MasterQuatationResponse masterQuatationResponse = masterQuatationResponseRepository.findById(masterQuatationResponseId)
               .orElseThrow(()-> new ResourceNotFoundException("MasterQuatationResponse id : "+masterQuatationResponseId+" is not found"));

       masterQuatationResponse = convertMasterResponseDtoIntoMasterQuatationResponse(masterQuatationResponse,masterQuatationResponseDto,currentUser);

       MasterQuatationResponse saveMasterQuatationResponse =  masterQuatationResponseRepository.save(masterQuatationResponse);

   //    Lead lead = masterQuatationResponse.getQuatationMaster().getQuatation().getLead();


//        // set leas status
//        lead.setCurrentStatus(LeadStatus.MASTER_RESPONSE_RECEIVED);
//        leadRepository.save(lead);
//
//
//        leadTimelineService.setLeadTimeLineFieldsAndSave(
//                lead,
////                LeadStage.PURCHASE,
////                LeadStage.PURCHASE,
////                LeadStatus.MASTER_RESPONSE_RECEIVED,
//                " Master Quotation Response Received : ",
//                currentUser,
//                ZonedDateTime.now(ZoneId.of("Asia/Kolkata"))
//        );

       return saveMasterQuatationResponse;
    }




    @Transactional
    public ApiResponse sendQuatationWithPricingToPricingExecutive(SendQuatationToPricingExecutiveRequest sendQuatationToPricingExecutiveRequest,User currentUser){

//        //get list of pricing executive by their Ids
//        Set<User> pricingExecutiveList = userRepository.findUsersByIds(sendQuatationToPricingExecutiveRequest.getPricingIds());

        // get quatation by id
        Quatation getQuatation = quatationRepository.findById(sendQuatationToPricingExecutiveRequest.getQuatationId())
                .orElseThrow(()-> new ResourceNotFoundException("Quatation with id : "+sendQuatationToPricingExecutiveRequest.getQuatationId()+" is not found"));

        //get list of quatation with master response
        List<QuatationMasterPricingResponseSqlDto> quatationMasterPricingResponseSqlDto = masterQuatationResponseRepository
                .findQuatationAndMasterPricingByQuatationId(sendQuatationToPricingExecutiveRequest.getQuatationId());

        if(quatationMasterPricingResponseSqlDto.isEmpty()){
            throw new InvalidLeadStateException("Cannot send lead to Pricing before receiving response from Master");
        }

        // Extract unique Lead & Quotation details from the first row
        QuatationMasterPricingResponseSqlDto firstEntryOfQuatation = quatationMasterPricingResponseSqlDto.get(0);

        //  Fetch already assigned PRICING executive IDs for this quotation
        Set<Long> alreadyAssignedPricingIds =
                quatationPricingExecutiveRepository
                        .findPricingExecutiveIdsByQuatationId(sendQuatationToPricingExecutiveRequest.getQuatationId());

        // 4️⃣ FILTER incoming request IDs (skip already sent)
        Set<Long> newPricingIds = sendQuatationToPricingExecutiveRequest.getPricingIds()
                .stream()
                .filter(id -> !alreadyAssignedPricingIds.contains(id))
                .collect(Collectors.toSet());

        if (newPricingIds.isEmpty()) {
            throw new InvalidLeadStateException(
                    "Quotation already sent to all selected Pricing Executives"
            );
        }

        //  Fetch Users
        Set<User> pricingExecutiveList =
                userRepository.findUsersByIds(newPricingIds);

//        // Format email content
//        String emailContent = buildPricingQuotationEmailContent(firstEntryOfQuatation, quatationMasterPricingResponseSqlDto);

        // Extract pricing executive emails
        List<String> recipientEmails = pricingExecutiveList.stream()
                .map(User::getEmail)  // Assuming User entity has getEmail() method
                .filter(Objects::nonNull)
                .toList();

        String pricingExcutiveNames = pricingExecutiveList.stream()
                .map(User::getUserName)
                .collect(Collectors.joining(", "));


        // Store Quotation-PricingExecutive Mapping & Send Email
        for (User pricingExecutive : pricingExecutiveList) {
            // Save in DB
            QuatationPricingExecutive quatationPricingExecutive = new QuatationPricingExecutive();
            quatationPricingExecutive.setQuatation(getQuatation);
            quatationPricingExecutive.setPricing_executive_id(pricingExecutive);

            ZonedDateTime istDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

            quatationPricingExecutive.setQuatationForwardDate(istDateTime.toLocalDate());
            quatationPricingExecutive.setQuatationForwardTime(istDateTime.toLocalTime());

            quatationPricingExecutiveRepository.save(quatationPricingExecutive);
        }

        //change the quatation status to sent-to-pricing-executive
        getQuatation.setQuatationStatus(QuatationStatus.SENT_TO_PRICING_EXECUTIVE);
        quatationRepository.save(getQuatation);


        // Send emails to pricing executives
        if (!recipientEmails.isEmpty()) {
          //  sendEmailService.sendBulkEmail(recipientEmails, "New Quotation for Pricing Review", emailContent);
            notificationService.sendEmailQuatationWithMasterPricingToPricingExcutives(recipientEmails,firstEntryOfQuatation,quatationMasterPricingResponseSqlDto);
        }


        leadStateService.performAction(
                getQuatation.getLead(),
                LeadEvent.SEND_TO_PRICING,
                LeadActionType.SEND_TO_PRICING,
                //  " Master Quotation Response Received ",
                "Quotation send to pricing executive : "+pricingExcutiveNames,
                currentUser
        );

        // ✅ Prepare Response Data
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("QuatationId",sendQuatationToPricingExecutiveRequest.getQuatationId());
        responseData.put("pricingExecutiveNotified", pricingExecutiveList.stream().map(User::getEmail).collect(Collectors.toSet()));

        return new ApiResponse(true,"Quotation request sent successfully",responseData);
    }


    public List<PurchaseQuatation> getQuatationWhichAreWeSendToPricingExecutive(Long purchaseExecutiveId){
       return quatationRepository.findQuatationDataWhichWeSendToPricingExecutiveByPurchaseExecutiveId(purchaseExecutiveId);
    }


    public List<SendQuatationPricingEmpResponse> viewPricingExecutiveListByQuatationId(@PathVariable("quatationId")Long quatationId){
       return quatationPricingExecutiveRepository.findPrincingEmpResponseByQuatationId(quatationId);
    }

//    private String buildEmailContentSendToMaster(Lead lead, String origin, String destination, LocalDate movingDate,
//                                     String commoditySize, String carName) {
//        return String.format(
//                "Quotation Request for Lead %s\nOrigin: %s\nDestination: %s\nMoving Date: %s\nCommodity Size: %s\nCar Name: %s",
//                lead.getLeadReferenceNo(), origin, destination, movingDate, commoditySize, carName);
//    }

//
//    private String buildPricingQuotationEmailContent(QuatationMasterPricingResponseSqlDto firstEntry,
//                                                     List<QuatationMasterPricingResponseSqlDto> allEntries) {
//        StringBuilder content = new StringBuilder();
//
//        content.append("Dear Pricing Executive,\n\n");
//        content.append("You have received a new quotation for pricing review.\n\n");
//
//        content.append("Quotation Details:\n");
//        content.append("Lead Reference No: ").append(firstEntry.getLeadReferenceNo()).append("\n");
//        content.append("Quotation Reference No: ").append(firstEntry.getQuatationReferenceNo()).append("\n");
//        content.append("Required Date: ").append(firstEntry.getQuatationRequiredDate()).append("\n");
//        content.append("Required Time: ").append(firstEntry.getQuatationRequiredTime()).append("\n");
//        content.append("Source: ").append(firstEntry.getSource()).append("\n");
//        content.append("Destination: ").append(firstEntry.getDestination()).append("\n");
//        content.append("Moving Date & Time: ").append(firstEntry.getMovingDateAndTime()).append("\n\n");
//
//        content.append("Master Responses:\n");
//        for (QuatationMasterPricingResponseSqlDto dto : allEntries) {
//            content.append("--------------------------------------\n");
//            content.append("Company: ").append(dto.getCompanyName()).append("\n");
//            content.append("Contact: ").append(dto.getContactName()).append(", ").append(dto.getContactNumber()).append("\n");
//            content.append("Email: ").append(dto.getEmailId()).append("\n");
//            content.append("Service Sector: ").append(dto.getServiceSector()).append("\n");
//            content.append("Location: ").append(dto.getLocation()).append("\n");
//            content.append("Hub: ").append(dto.getHub()).append("\n");
//            content.append("State: ").append(dto.getState()).append("\n");
//            content.append("Original Package Cost: ₹").append(dto.getOriginalPackageCost()).append("\n");
//            content.append("Final Package Cost: ₹").append(dto.getFinalPackageCost()).append("\n\n");
//        }
//
//        content.append("Please review the above details and proceed with the pricing decision.\n\n");
//        content.append("Best Regards,\nMex Cargo");
//
//        return content.toString();
//    }


    private MasterQuatationResponse convertMasterResponseDtoIntoMasterQuatationResponse(
            MasterQuatationResponse response,MasterQuatationResponseDto dto,User currentUser
    ){
        response.setOriginalPackageCost(dto.getOriginalPackageCost());
        response.setOriginalTrsCost(dto.getOriginalTrsCost());
        response.setOriginalCarServiceCost(dto.getOriginalCarServiceCost());
        response.setOriginalAdditionalServiceCost(dto.getOriginalAdditionalCost());
        response.setFinalPackageCost(dto.getFinalPackageCost());
        response.setFinalTrsCost(dto.getFinalTrsCost());
        response.setFinalCarServiceCost(dto.getFinalCarServiceCost());
        response.setFinalAdditionalServiceCost(dto.getFinalAdditionalCost());

        ZonedDateTime istDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        response.setResponseDate(istDateTime.toLocalDateTime());
      //  response.setResponseAddedBy(currentUser);
        response.setLastModifiedBy(currentUser);
        response.setLastModifiedAt(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));

        return response;
    }




}
