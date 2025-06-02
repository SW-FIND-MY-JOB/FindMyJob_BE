package com.example.correctionservice.domain.correction.controller;

import com.example.correctionservice.domain.correction.dto.CorrectionReqDTO;
import com.example.correctionservice.domain.correction.dto.CorrectionResDTO;
import com.example.correctionservice.domain.correction.exception.status.CorrectionSuccessStatus;
import com.example.correctionservice.domain.correction.service.CorrectionService;
import com.example.responselib.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/correction")
@Tag(name = "자소서 첨삭 관련 API입니다", description = "자소서 첨삭 관련 API입니다")
public class CorrectionController {
    private final CorrectionService correctionService;

    //사용자가 쓴 문장과 다른 사람이 쓴 자소서Id를 받아서 gpt에게 넘기고 첨삭을 반환
    @PostMapping
    @Operation(summary = "AI에게 자소서 피드백", description = "AI에게 자소서 피드백 받기")
    public ApiResponse<List<CorrectionResDTO.FeedbackResDTO>> correction(HttpServletRequest request, @RequestBody @Valid CorrectionReqDTO.CorrectionReqInform correctionReqInform){
        List<CorrectionResDTO.FeedbackResDTO> feedbackResDTOList = correctionService.getCorrection(request, correctionReqInform);
        return ApiResponse.of(CorrectionSuccessStatus._SUCCESS_AI_CORRECTION_RESPONSE, feedbackResDTOList);
    }
}
