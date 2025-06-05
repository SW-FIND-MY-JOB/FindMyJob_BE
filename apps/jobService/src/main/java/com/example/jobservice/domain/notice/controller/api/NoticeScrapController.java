package com.example.jobservice.domain.notice.controller.api;

import com.example.jobservice.domain.notice.dto.notice.NoticeResDTO;
import com.example.jobservice.domain.notice.dto.scrapNotice.NoticeScrapResDTO;
import com.example.jobservice.domain.notice.exception.status.NoticeSuccessStatus;
import com.example.jobservice.domain.notice.service.NoticeScrapService;
import com.example.responselib.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/notice-scraps")
@RequiredArgsConstructor
@Tag(name = "채용 공고 스크랩 관련 API입니다", description = "채용 공고 스크랩 관련 API입니다")
public class NoticeScrapController {
    private final NoticeScrapService noticeScrapService;

    //스크랩한 정보 보여주기
    @GetMapping
    @Operation(summary = "채용 공고 가져오기", description = "스크랩한 정보 보여주기")
    public ApiResponse<Page<NoticeResDTO.NoticeInformDTO>> getScrapNotice(
            HttpServletRequest request,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {

        Page<NoticeResDTO.NoticeInformDTO> noticeInformDTOS = noticeScrapService.searchNoticeScrap(request, page, size);
        return ApiResponse.of(NoticeSuccessStatus._SUCCESS_GET_NOTICE_SCRAP_INFORM, noticeInformDTOS);
    }

    //스크랩한 정보 월별로 알려주기
    @GetMapping("/date")
    @Operation(summary = "스크랩한 채용공고 월별로 가져오기", description = "스크랩한 채용공고 달력에 맞춰서 년/월 별로 정보 가져오기")
    public ApiResponse<List<NoticeScrapResDTO.CalendarNoticeInformDTO>> getScrapCalendarNotice(
            HttpServletRequest request,
            @RequestParam(name = "year") int year,
            @RequestParam(name = "month") int month) {

        List<NoticeScrapResDTO.CalendarNoticeInformDTO> calendarNoticeInformDTOList = noticeScrapService.searchDateNoticeScrap(request, year, month);
        return ApiResponse.of(NoticeSuccessStatus._SUCCESS_GET_NOTICE_SCRAP_INFORM, calendarNoticeInformDTOList);
    }

    //스크랩하기
    //헤더에서 토큰 추출하여 사용자 식별
    @PostMapping
    @Operation(summary = "스크랩하기", description = "스크랩하기")
    public ApiResponse<Null> scrapNotice(HttpServletRequest request,
                                         @RequestParam Long noticeId) {
        noticeScrapService.saveNoticeScrap(request,noticeId);
        return ApiResponse.of(NoticeSuccessStatus._SUCCESS_POST_NOTICE_SCRAP);
    }

    //스크랩 해제하기
    @DeleteMapping
    @Operation(summary = "스크랩 해제하기", description = "스크랩 해제하기")
    public ApiResponse<Null> deleteScrapNotice(HttpServletRequest request,
                                               @RequestParam Long noticeId) {
        noticeScrapService.deleteNoticeScrap(request,noticeId);
        return ApiResponse.of(NoticeSuccessStatus._SUCCESS_DELETE_NOTICE_SCRAP);
    }
}
