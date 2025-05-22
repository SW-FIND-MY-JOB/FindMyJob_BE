package com.example.jobservice.domain.notice.controller;

import com.example.jobservice.domain.notice.dto.notice.NoticeResDTO;
import com.example.jobservice.domain.notice.exception.status.NoticeSuccessStatus;
import com.example.jobservice.domain.notice.service.NoticeService;
import com.example.responselib.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
@Tag(name = "채용 공고 관련 API입니다", description = "채용 공고 관련 API입니다")
public class NoticeController {
    private final NoticeService noticeService;

    //조건 검색으로 정보 가져오기 (페이징 처리)
    @GetMapping("/inform")
    @Operation(summary = "채용 공고 가져오기", description = "조건 검색으로 정보 가져오기 (페이징 처리)")
    public ApiResponse<Page<NoticeResDTO.NoticeInformDTO>> getNoticeInforms(
            @RequestParam(name = "region", defaultValue = "all") String region,
            @RequestParam(name = "category", defaultValue = "all") String category,
            @RequestParam(name = "history", defaultValue = "all") String history,
            @RequestParam(name = "edu", defaultValue = "all") String edu,
            @RequestParam(name = "type", defaultValue = "all") String type,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size){

        Page<NoticeResDTO.NoticeInformDTO> noticeInformDTOS = noticeService.searchNotices(region, category, history, edu, type, keyword, page, size);

        return ApiResponse.of(NoticeSuccessStatus._SUCCESS_GET_NOTICE_INFORM, noticeInformDTOS);
    }

    //최근 올라온 공고 보여주기
    @GetMapping("/recent-inform")
    @Operation(summary = "채용 공고 가져오기", description = "최근 올라온 공고 보여주기")
    public ApiResponse<Page<NoticeResDTO.NoticeInformDTO>> getRecentNoticeInforms(
            HttpServletRequest request,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {

        Page<NoticeResDTO.NoticeInformDTO> noticeInformDTOS = noticeService.searchRecentNotices(request, page, size);

        return ApiResponse.of(NoticeSuccessStatus._SUCCESS_GET_NOTICE_INFORM, noticeInformDTOS);
    }
}
