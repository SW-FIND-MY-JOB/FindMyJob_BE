package com.example.jobservice.domain.notice.service;

import com.example.jobservice.domain.notice.converter.NoticeConverter;
import com.example.jobservice.domain.notice.dto.notice.NoticeResDTO;
import com.example.jobservice.domain.notice.entity.Notice;
import com.example.jobservice.domain.notice.exception.status.NoticeErrorStatus;
import com.example.jobservice.domain.notice.repository.NoticeRepository;
import com.example.jobservice.domain.notice.repository.NoticeScrapRepository;
import com.example.jobservice.global.exception.GeneralException;
import com.example.jobservice.global.util.TokenUtil;
import com.example.jwtutillib.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final NoticeScrapRepository noticeScrapRepository;
    private final TokenUtil tokenUtl;
    private final JwtUtil jwtUtil;

    //공고 조건검색
    public Page<NoticeResDTO.NoticeInformDTO> searchNotices(String region, String category, String history, String edu,
                                      String type, String keyword, int page, int size){
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Notice> result = noticeRepository.searchNotices(region, category, history, edu, type, keyword, pageable);
        return result.map(dto -> NoticeConverter.toNoticeResDTO(dto, false));
    }

    //단일 공고 조회
    @Transactional
    public NoticeResDTO.NoticeDetailInformDTO searchNotice(HttpServletRequest request, Long noticeId){
        // 사용자 id 파싱
        Long userId = tokenUtl.getUserId(request);

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new GeneralException(NoticeErrorStatus._NOT_EXIST_NOTICE));

        //조회수 증가
        notice.setViewCnt(notice.getViewCnt() + 1);
        noticeRepository.save(notice);

        //로그인 x
        if (userId == null){
            return NoticeConverter.toNoticeDetailResDTO(notice, false);
        }

        //로그인 o 스크랩 했는지 확인
        boolean isScrap = noticeScrapRepository.existsByNoticeAndUserId(notice, userId);
        return NoticeConverter.toNoticeDetailResDTO(notice, isScrap);
    }

    //최근 공고검색
    public Page<NoticeResDTO.NoticeInformDTO> searchRecentNotices(HttpServletRequest request, int page, int size){
        // 사용자 id 파싱
        Long userId = tokenUtl.getUserId(request);

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Notice> result = noticeRepository.findAllByOrderByCreatedAtDesc(pageable);

        // 사용자가 null이라면
        if (userId == null) {
            return result.map(dto -> NoticeConverter.toNoticeResDTO(dto, false));
        }

        return result.map(notice -> {
            boolean isScrap = noticeScrapRepository.existsByNoticeAndUserId(notice, userId);
            return NoticeConverter.toNoticeResDTO(notice, isScrap);
        });
    }
}
