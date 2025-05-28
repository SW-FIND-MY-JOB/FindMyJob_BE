package com.example.jobservice.domain.agency.controller;

import com.example.jobservice.domain.agency.dto.AgencyReqDTO;
import com.example.jobservice.domain.agency.dto.AgencyResDTO;
import com.example.jobservice.domain.agency.exception.status.AgencySuccessStatus;
import com.example.jobservice.domain.agency.service.AgencyService;
import com.example.responselib.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/agency")
@RequiredArgsConstructor
@Tag(name = "기업 정보 관련 API입니다", description = "기업 정보 관련 API입니다")
public class AgencyController {
    private final AgencyService agencyService;

    //Get 기관명으로 get
    @GetMapping("/informs")
    @Operation(summary = "기관 정보 가져오기", description = "모든 기관 정보 가져오기")
    public ApiResponse<List<AgencyResDTO.AgencyInformDTO>> getAllAgency(){
        List<AgencyResDTO.AgencyInformDTO> agencyInformDTOList = agencyService.getAllAgencyInform();

        return ApiResponse.of(AgencySuccessStatus._SUCCESS_GET_AGENCY_INFORM, agencyInformDTOList);
    }

    //Get 기관명으로 get
    @GetMapping("/inform")
    @Operation(summary = "기관 정보 (기관명으로) 가져오기", description = "기관 정보(기관명으로) 가져오기")
    public ApiResponse<AgencyResDTO.AgencyInformDTO> getAgency(@RequestParam("name") String name){
        AgencyResDTO.AgencyInformDTO agencyInformDTO = agencyService.getAgencyInform(name);

        return ApiResponse.of(AgencySuccessStatus._SUCCESS_GET_AGENCY_INFORM, agencyInformDTO);
    }

    //Post
    @PostMapping("/inform")
    @Operation(summary = "기관 정보 저장하기", description = "기관 정보 저장하기")
    public ApiResponse<Null> saveAgency(@RequestBody @Valid List<AgencyReqDTO.AgencyInformDTO> agencyInformDTOList){
        agencyService.saveAgencyInform(agencyInformDTOList);

        return ApiResponse.of(AgencySuccessStatus._SUCCESS_SAVE_AGENCY_INFORM, null);
    }

    //Patch
    @PatchMapping("/inform")
    @Operation(summary = "기관 정보(기관명으로) 수정하기", description = "기관 정보(기관명으로) 수정하기")
    public ApiResponse<Null> updateAgency(@PathVariable String name,
                                          @RequestBody AgencyReqDTO.AgencyInformDTO agencyInformDTO){
        agencyService.updateAgencyInform(name, agencyInformDTO);
        return ApiResponse.of(AgencySuccessStatus._SUCCESS_UPDATE_AGENCY_INFORM, null);
    }

    //Delete
    @DeleteMapping("/inform")
    @Operation(summary = "기관 정보(기관명으로) 삭제하기", description = "기관 정보(기관명으로) 삭제하기")
    public ApiResponse<Null> deleteAgency(@PathVariable String name){
        agencyService.deleteAgencyInform(name);

        return ApiResponse.of(AgencySuccessStatus._SUCCESS_DELETE_AGENCY_INFORM, null);
    }
}
