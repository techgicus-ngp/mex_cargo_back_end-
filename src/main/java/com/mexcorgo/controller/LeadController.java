package com.mexcorgo.controller;


import com.mexcorgo.datamodel.*;
import com.mexcorgo.dto.FollowUpDto.FollowUpBasicResponse;
import com.mexcorgo.dto.FollowUpDto.FollowUpRequest;
import com.mexcorgo.dto.companyDto.CompanyRequest;
import com.mexcorgo.dto.companyEmployeeDto.CompanyEmployeeRequest;
import com.mexcorgo.dto.leadDto.CreateLeadRequest;
import com.mexcorgo.dto.leadDto.LeadFullResponse;
import com.mexcorgo.dto.leadDto.LeadResponse;
import com.mexcorgo.dto.needDto.NeedRequest;
import com.mexcorgo.dto.request.*;
import com.mexcorgo.dto.response.GetLeadFullDetails;
import com.mexcorgo.dto.response.LeadResponseDto;
import com.mexcorgo.security.MyUserDetails;

import com.mexcorgo.service.lead.LeadService;
import com.mexcorgo.service.lead.company.CompanyService;
import com.mexcorgo.service.lead.companyEmployee.CompanyEmployeeService;
import com.mexcorgo.service.lead.endUserDetails.EndUserDetailsService;
import com.mexcorgo.service.lead.need.NeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LeadController {

     private final LeadService leadService;
     private final CompanyService companyService;
     private final CompanyEmployeeService companyEmployeeService;
     private final EndUserDetailsService endUserDetailsService;
     private final NeedService needService;


    @PostMapping("/created/lead")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public ResponseEntity<Lead> createdNewLead(@RequestBody CreateLeadRequest request, Authentication authentication){
        User currentUser = ((MyUserDetails) authentication.getPrincipal()).getUser();
       return ResponseEntity.ok(leadService.createLead(request,currentUser));
    }


    @GetMapping("/get/currentuser/lead")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public Page<LeadResponseDto> getUserLeads(
            @RequestParam Long userId,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size
    ){
         return leadService.getUserLeads(userId,page,size);
    }


      @GetMapping("/get/lead/{leadId}/company/details")
      @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
      public ResponseEntity<Company> getCompanyDetailsForLead(@PathVariable("leadId")Long leadId){
        return ResponseEntity.ok(companyService.getCompany(leadId));
      }


      @GetMapping("/get/lead/{leadId}/company/emp/details")
      @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
      public ResponseEntity<CompanyEmployee> getCompanyEmployeeDetailsForLead(@PathVariable("leadId")Long leadId){
         return ResponseEntity.ok(companyEmployeeService.getCompanyEmployee(leadId));
      }

    @GetMapping("/get/lead/{leadId}/end-user/details")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public ResponseEntity<EndUserDetails> getEndUserDetails(@PathVariable("leadId")Long leadId){
        return ResponseEntity.ok(endUserDetailsService.getEndUser(leadId));
    }

      @GetMapping("/get/lead/{leadId}/need")
      @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
      public ResponseEntity<Need> getNeedForLead(@PathVariable("leadId")Long leadId){
         return ResponseEntity.ok(needService.getNeed(leadId));
      }



      @DeleteMapping("/delete/lead/{leadId}")
      @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
      public ResponseEntity<Boolean> deleteTheLead(@PathVariable("leadId")Long leadId){
        return ResponseEntity.ok(leadService.deleteTheLead(leadId));
      }

      @GetMapping("/get/lead/{leadId}")
      @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
      public ResponseEntity<LeadResponseDto> getLeadById(@PathVariable("leadId")Long leadId){
        return ResponseEntity.ok(leadService.getLeadById(leadId));
      }


//      @PutMapping("/update/lead/{leadId}")
//      @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
//      public ResponseEntity<LeadFullResponse> updateLeadDetails(@PathVariable("leadId")Long leadId, @RequestBody LeadResponse request){
//        return ResponseEntity.ok(leadService.updateLeadDetails(leadId,request));
//      }


      @PutMapping("/update/lead/{leadId}/company/details")
      @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
      public Company updateCompanyDetails(@PathVariable("leadId")Long leadId,@RequestBody CompanyRequest companyRequest){
        return companyService.updateCompany(leadId,companyRequest);
      }

    @PutMapping("/update/lead/{leadId}/company/emp/details")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public CompanyEmployee updateCompanyEmployeeDetails(@PathVariable("leadId")Long leadId,@RequestBody CompanyEmployeeRequest companyEmplyeeRequest){
        return companyEmployeeService.updateCompanyEmployee(leadId,companyEmplyeeRequest);
    }

    @PutMapping("/update/lead/{leadId}/need/details")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public Need updateNeedDetails(@PathVariable("leadId")Long leadId, @RequestBody NeedRequest needRequest){
        return needService.updateNeed(leadId,needRequest);
    }

    @GetMapping("/lead/{leadId}/full-details")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public LeadFullResponse getLeadFullDetails(@PathVariable("leadId") Long leadId){
        return leadService.getFullLeadDetails(leadId);
    }

    @PutMapping("/update/lead/{leadId}/full-details")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public LeadFullResponse updateLeadFullDetails(@PathVariable("leadId")Long leadId,@RequestBody LeadFullResponse request){
        return leadService.updateFullLeadDetails(leadId,request);
    }

    @GetMapping("/lead/{leadId}/end-user-details")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public EndUserDetails getEndUserByLeadId(@PathVariable("leadId")Long leadId){
        return endUserDetailsService.getEndUser(leadId);
    }

    @GetMapping("/get/lead/{leadId}/particular-amount")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public QuatationParticularsAmount getQuatationParticularAmount(@PathVariable("leadId")Long leadId){
        return leadService.getParticularAmount(leadId);
    }


    @PutMapping("/update/lead/{leadId}/particular-amount")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public QuatationParticularsAmount updateQuatationParticularAmount(@PathVariable("leadId")Long leadId,
                                                                      @RequestBody AddParticularsRequestDto addParticularsRequestDto){
        return leadService.updateParticularAmount(leadId,addParticularsRequestDto);
    }

//          @GetMapping("/get/lead/{leadId}/note")
//      @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
//      public ResponseEntity<Note> getNoteForLead(@PathVariable("leadId")Long leadId){
//         return ResponseEntity.ok(leadService.getNoteForLead(leadId));
//      }

//    @PutMapping("/update/lead/{leadId}/note/details")
//    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
//    public Note updateNoteDetails(@PathVariable("leadId")Long leadId, @RequestBody NoteRequest noteRequest){
//        return leadService.updateNote(leadId,noteRequest);
//    }



    @PostMapping("/add/lead/{leadId}/extra-need-data")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public NeedExtraDetails afterLeadConfirmAddExtraNeedData(@PathVariable("leadId")Long leadId,@RequestBody NeedExtraDetailsRequestDto needExtraDetailsRequestDto){
     return leadService.afterLeadConfirmAddExtraNeedData(leadId,needExtraDetailsRequestDto);
    }


    @GetMapping("/get/lead/{leadId}/extra-need-details")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public NeedExtraDetails getExtraNeedDetails(@PathVariable("leadId")Long leadId){
       return leadService.getExtraNeedDetails(leadId);
    }

    @PutMapping("/update/lead/{leadId}/extra-need-details")
    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
    public NeedExtraDetails updateExtraNeedDetails(@PathVariable("leadId")Long leadId,@RequestBody NeedExtraDetails needExtraDetails){
        return leadService.updateExtraNeedDetails(leadId,needExtraDetails);
    }




//    @GetMapping("/get/all/lead/according")
//    @PreAuthorize("hasAnyRole('Admin','Head','Leader','Member')")
//    public ResponseEntity<List<LeadResponseDto>> getLeadByFollowUpDate(@RequestParam String followUpDate){
//        return ResponseEntity.ok(leadService.findLeadByFollowUpDate(LocalDate.parse(followUpDate)));
//    }






}
