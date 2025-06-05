package com.example.coverletterservice.domain.coverLetter.controller.api;

import com.example.coverletterservice.domain.coverLetter.dto.CoverLetterResDTO;
import com.example.coverletterservice.domain.coverLetter.exception.status.CoverLetterSuccessStatus;
import com.example.coverletterservice.domain.coverLetter.service.CoverLetterScrapService;
import com.example.responselib.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cover-letter-scraps")
@Tag(name = "자소서 스크랩 관련 API입니다", description = "자소서 스크랩 관련 API입니다")
public class CoverLetterScrapController {
    private final CoverLetterScrapService coverLetterScrapService;

    //스크랩한 정보 보여주기
    @GetMapping
    @Operation(summary = "스크랩한 자소서 가져오기", description = "스크랩한 자소서 가져오기")
    public ApiResponse<Page<CoverLetterResDTO.CoverLetterInformDTO>> searchScrapCoverLetter(HttpServletRequest request,
                                                                       @RequestParam(name = "page", defaultValue = "1") int page,
                                                                       @RequestParam(name = "size", defaultValue = "5") int size){
        Page<CoverLetterResDTO.CoverLetterInformDTO> coverLetterInformDTOList = coverLetterScrapService.searchCoverLetterScrap(request, page, size);
        return ApiResponse.of(CoverLetterSuccessStatus._SUCCESS_GET_COVER_LETTER, coverLetterInformDTOList);
    }

    //스크랩한 정보 모두 보여주기
    @GetMapping("/all")
    @Operation(summary = "스크랩한 자소서 모두 가져오기", description = "스크랩한 자소서 모두 가져오기")
    public ApiResponse<List<CoverLetterResDTO.CoverLetterInformDTO>> searchAllScrapCoverLetter(HttpServletRequest request){
        List<CoverLetterResDTO.CoverLetterInformDTO> coverLetterInformDTOList = coverLetterScrapService.searchAllCoverLetterScrap(request);
        return ApiResponse.of(CoverLetterSuccessStatus._SUCCESS_GET_COVER_LETTER, coverLetterInformDTOList);
    }


    //스크랩하기
    @PostMapping
    @Operation(summary = "자소서 스크랩하기", description = "자소서 스크랩하기")
    public ApiResponse<Null> scrapCoverLetter(HttpServletRequest request,
                                              @RequestParam Long coverLetterId){
        coverLetterScrapService.saveCoverLetterScrap(request, coverLetterId);
        return ApiResponse.of(CoverLetterSuccessStatus._SUCCESS_SCRAP_COVER_LETTER);
    }

    // 스크랩 해제하기
    @DeleteMapping
    @Operation(summary = "스크랩 해제하기", description = "자소서 스크랩 해제하기")
    public ApiResponse<Null> deleteScrapCoverLetter(HttpServletRequest request,
                                                    @RequestParam Long coverLetterId){
        coverLetterScrapService.deleteCoverLetterScrap(request, coverLetterId);
        return ApiResponse.of(CoverLetterSuccessStatus._SUCCESS_DELETE_SCRAP_COVER_LETTER);
    }
}
