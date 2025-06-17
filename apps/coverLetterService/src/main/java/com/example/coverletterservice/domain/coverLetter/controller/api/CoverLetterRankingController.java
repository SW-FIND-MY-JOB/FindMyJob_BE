package com.example.coverletterservice.domain.coverLetter.controller.api;

import com.example.coverletterservice.domain.coverLetter.dto.CoverLetterResDTO;
import com.example.coverletterservice.domain.coverLetter.exception.status.CoverLetterSuccessStatus;
import com.example.coverletterservice.domain.coverLetter.service.CoverLetterService;
import com.example.responselib.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cover-letter-rankings")
@Tag(name = "자소서 랭킹 관련 API입니다", description = "자소서 랭킹 API입니다")
public class CoverLetterRankingController {
    private final CoverLetterService coverLetterService;

    //자소서 랭킹 조회
    @GetMapping
    @Operation(summary = "이번주 자소서 랭킹 조회", description = "자소서 랭킹 조회하기")
    public ApiResponse<List<CoverLetterResDTO.CoverLetterRankingResDTO>> getCoverLetterTop10Inform(){
        List<CoverLetterResDTO.CoverLetterRankingResDTO> dtoList = coverLetterService.searchCoverLetterRanking();

        return ApiResponse.of(CoverLetterSuccessStatus._SUCCESS_GET_WEEK_RANKING, dtoList);
    }
}
