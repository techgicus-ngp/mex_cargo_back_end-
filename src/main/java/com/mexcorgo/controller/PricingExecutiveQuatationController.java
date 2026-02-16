package com.mexcorgo.controller;


import com.mexcorgo.datamodel.User;
import com.mexcorgo.dto.request.AddQuatationAnalyzePricingDto;
import com.mexcorgo.dto.request.FinalizeQuatationRequest;
import com.mexcorgo.dto.response.*;
import com.mexcorgo.security.MyUserDetails;
import com.mexcorgo.service.PricingExecutiveQuatationService;
import org.mapstruct.control.MappingControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/pricingexceutive/quatation")
public class PricingExecutiveQuatationController {

     private PricingExecutiveQuatationService pricingExecutiveQuatationService;

    @Autowired
    public PricingExecutiveQuatationController(PricingExecutiveQuatationService pricingExecutiveQuatationService) {
        this.pricingExecutiveQuatationService = pricingExecutiveQuatationService;
    }


    @GetMapping("/pricingexceutive/quatation/data/{pricingExecutiveId}")
    @PreAuthorize("hasAnyRole('Admin','Head','Pricing Executive')")
    public List<PricingQuatation> getQuatationsByPricingExecutiveId(@PathVariable("pricingExecutiveId")Long pricingExecutiveId){
      return pricingExecutiveQuatationService.getQuatationByPricingExecutiveId(pricingExecutiveId);
    }


    @GetMapping("/pricingexceutive/quatation/{quatationId}/get/masters-with-response")
    @PreAuthorize("hasAnyRole('Admin','Head','Pricing Executive','Planning Executive')")
    public List<QuatationMasterWithMasterResponse> getQuatationMasterAndMasterResponseByQuatationId(@PathVariable("quatationId")Long quatationId){
       return pricingExecutiveQuatationService.getQuatationMasterWithMasterResponseByQuatationId(quatationId);
    }


    @PostMapping("/pricingexceutive/quatation/{quatationId}/add-analyze-pricing")
    @PreAuthorize("hasAnyRole('Admin','Head','Pricing Executive')")
    public AddQuatationAnalyzePricingResponse addQuatationAnalyzePricing(@PathVariable("quatationId")Long quatationId, @RequestBody AddQuatationAnalyzePricingDto addQuatationAnalyzePricingDto,
                                                                         Authentication authentication){
        User currentUser = ((MyUserDetails) authentication.getPrincipal()).getUser();
      return pricingExecutiveQuatationService.addQuatationAnalyzePricing(quatationId,addQuatationAnalyzePricingDto,currentUser);
    }



    @PutMapping("/pricingexceutive/quatation/{quatationId}/update-analyze-pricing")
    @PreAuthorize("hasAnyRole('Admin','Head','Pricing Executive')")
    public AddQuatationAnalyzePricingResponse updateQuatationAnalyzePricing(@PathVariable("quatationId")Long quatationId, @RequestBody AddQuatationAnalyzePricingDto addQuatationAnalyzePricingDto){
        return pricingExecutiveQuatationService.upadteQuatationAnalyzePricing(quatationId,addQuatationAnalyzePricingDto);
    }

//    @PostMapping("/{quatationId}/finalize")
//    public ApiResponse finalizedQuatation(@PathVariable("quatationId")Long quatationId,
//                                          @RequestBody FinalizeQuatationRequest finalizeQuatationRequest){
//        return pricingExecutiveQuatationService.finalizedQuatation(quatationId,finalizeQuatationRequest);
//    }


    @PostMapping("/pricingexceutive/quatation/{quatationId}/analyze-pricing/send-to-creator")
    @PreAuthorize("hasAnyRole('Admin','Head','Pricing Executive')")
    public ResponseEntity<ApiResponse> sendQuatationAnalyzePricingToQuatationCreator(@PathVariable("quatationId")Long quatationId,Authentication authentication){
        User currentUser = ((MyUserDetails) authentication.getPrincipal()).getUser();
       return  ResponseEntity.ok(pricingExecutiveQuatationService.sendQuatationAnalyzePricingToQuatationCreator(quatationId,currentUser));
    }



}
