package com.example.jobservice.domain.notice.controller.api;

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
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@Tag(name = "채용 공고 관련 API입니다", description = "채용 공고 관련 API입니다")
public class NoticeController {
    private final NoticeService noticeService;

    //조건 검색으로 정보 가져오기 (페이징 처리)
    @GetMapping("/informs")
    @Operation(summary = "채용 공고 가져오기", description = "조건 검색으로 정보 가져오기 (페이징 처리)")
    public ApiResponse<Page<NoticeResDTO.NoticeInformDTO>> getNoticeInforms(
            HttpServletRequest request,
            //근무지역 코드
            @RequestParam(name = "regionCD", defaultValue = "all") String region,
            //직무 코드
            @RequestParam(name = "categoryCD", defaultValue = "all") String category,
            //경력 코드
            @RequestParam(name = "historyCD", defaultValue = "all") String history,
            //학력 코드
            @RequestParam(name = "eduCD", defaultValue = "all") String edu,
            //채용유형(정규직) 코드
            @RequestParam(name = "typeCD", defaultValue = "all") String type,
            //키워드
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size){

        Page<NoticeResDTO.NoticeInformDTO> noticeInformDTOS = noticeService.searchNotices(request, region, category, history, edu, type, keyword, page, size);

        return ApiResponse.of(NoticeSuccessStatus._SUCCESS_GET_NOTICE_INFORM, noticeInformDTOS);
    }

    //단일 공고 조회하기
    @GetMapping("/inform")
    @Operation(summary = "채용 공고 가져오기", description = "단일 공고 정보 가져오기")
    public ApiResponse<?> getNoticeInform(HttpServletRequest request,
                                          @RequestParam Long noticeId){
        NoticeResDTO.NoticeDetailInformDTO noticeDetailInformDTO = noticeService.searchNotice(request, noticeId);
        return ApiResponse.of(NoticeSuccessStatus._SUCCESS_GET_NOTICE_INFORM, noticeDetailInformDTO);
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
