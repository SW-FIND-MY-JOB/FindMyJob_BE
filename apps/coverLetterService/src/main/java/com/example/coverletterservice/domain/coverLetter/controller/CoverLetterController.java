package com.example.coverletterservice.domain.coverLetter.controller;

import com.example.coverletterservice.domain.coverLetter.dto.CoverLetterReqDTO;
import com.example.coverletterservice.domain.coverLetter.dto.CoverLetterResDTO;
import com.example.coverletterservice.domain.coverLetter.exception.status.CoverLetterSuccessStatus;
import com.example.coverletterservice.domain.coverLetter.service.CoverLetterService;
import com.example.responselib.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cover-letters")
@Tag(name = "자소서 관련 API입니다", description = "자소서 관련 API입니다")
public class CoverLetterController {
    private final CoverLetterService coverLetterService;

    //자소서 작성
    @PostMapping
    @Operation(summary = "자소서 작성", description = "자소서 작성하기")
    public ApiResponse<Null> createCoverLetter(HttpServletRequest request, @RequestBody @Valid CoverLetterReqDTO.CoverLetterInformDTO coverLetterInformDTO){
        coverLetterService.createCoverLetter(request, coverLetterInformDTO);
        return ApiResponse.of(CoverLetterSuccessStatus._SUCCESS_CREATE_COVER_LETTER);
    }

    //자소서 단일 조회
    @GetMapping
    @Operation(summary = "자소서 검색", description = "자소서 검색하기")
    public ApiResponse<CoverLetterResDTO.CoverLetterDetailInformDTO> getCoverLetterDetailInform(HttpServletRequest request, @RequestParam Long coverLetterId){
        CoverLetterResDTO.CoverLetterDetailInformDTO coverLetterDetailInformDTO = coverLetterService.SearchCoverLetter(request, coverLetterId);
        return ApiResponse.of(CoverLetterSuccessStatus._SUCCESS_GET_COVER_LETTER, coverLetterDetailInformDTO);
    }

    //자소서 조건 검색
    @GetMapping("/search")
    @Operation(summary = "자소서 검색", description = "자소서 조건으로 검색하기 (페이징 처리)")
    public ApiResponse<Page<CoverLetterResDTO.CoverLetterInformDTO>> getCoverLetterConditionInform (HttpServletRequest request,
                                                                                                    @RequestParam(defaultValue = "all") String instNm,
                                                                                                     @RequestParam(defaultValue = "all") String category,
                                                                                                     @RequestParam(defaultValue = "") String keyword,
                                                                                                     @RequestParam(defaultValue = "1") int page,
                                                                                                     @RequestParam(defaultValue = "5") int size){
        Page<CoverLetterResDTO.CoverLetterInformDTO> coverLetterConditionInformDTOList = coverLetterService.searchCoverLetters(request, instNm, category, keyword, page, size);
        return ApiResponse.of(CoverLetterSuccessStatus._SUCCESS_GET_COVER_LETTER, coverLetterConditionInformDTOList);
    }

    //자소서 삭제
    @DeleteMapping
    @Operation(summary = "자소서 삭제", description = "자소서 삭제하기")
    public ApiResponse<Null> deleteCoverLetter(HttpServletRequest request, @RequestParam Long coverLetterId){
        coverLetterService.deleteCoverLetter(request, coverLetterId);
        return ApiResponse.of(CoverLetterSuccessStatus._SUCCESS_DELETE_COVER_LETTER);
    }
}
