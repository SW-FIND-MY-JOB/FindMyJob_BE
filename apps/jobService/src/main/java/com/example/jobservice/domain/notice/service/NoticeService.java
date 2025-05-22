package com.example.jobservice.domain.notice.service;

import com.example.jobservice.domain.notice.converter.NoticeConverter;
import com.example.jobservice.domain.notice.dto.notice.NoticeResDTO;
import com.example.jobservice.domain.notice.entity.Notice;
import com.example.jobservice.domain.notice.repository.NoticeRepository;
import com.example.jobservice.domain.notice.repository.NoticeScrapRepository;
import com.example.jwtutillib.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final NoticeScrapRepository noticeScrapRepository;
    private final JwtUtil jwtUtil;

    //공고 조건검색
    public Page<NoticeResDTO.NoticeInformDTO> searchNotices(String region, String category, String history, String edu,
                                      String type, String keyword, int page, int size){
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Notice> result = noticeRepository.searchNotices(region, category, history, edu, type, keyword, pageable);
        return result.map(NoticeConverter::toNoticeResDTO);
    }

    //최근 공고검색
    public Page<NoticeResDTO.NoticeInformDTO> searchRecentNotices(HttpServletRequest request, int page, int size){

        //사용자 정보 추출
        String token = request.getHeader("Authorization");

        //사용자 id 초기화
        Long userId;

        //토큰 검증
        if (token != null && token.startsWith("Bearer ")){
            token = token.substring(7);

            if(!jwtUtil.isExpired(token)){
                userId = jwtUtil.getUserId(token);
            } else {
                userId = null;
            }
        } else {
            userId = null;
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Notice> result = noticeRepository.findAllByOrderByCreatedAtDesc(pageable);

        // 사용자가 null이라면
        if (userId == null) {
            return result.map(NoticeConverter::toNoticeResDTO);
        }

        // 사용자가 null이 아니라면
        return result.map(notice -> {
            boolean isScrap = noticeScrapRepository.existsByNoticeAndUserId(notice, userId);

            return NoticeResDTO.NoticeInformDTO.builder()
                    .id(notice.getId())
                    .instNm(notice.getInstNm())
                    .ncsCdNmLst(notice.getNcsCdNmLst())
                    .hireTypeNmLst(notice.getHireTypeNmLst())
                    .workRgnNmLst(notice.getWorkRgnNmLst())
                    .recruitSeNm(notice.getRecruitSeNm())
                    .pbancEndYmd(notice.getPbancEndYmd())
                    .recrutPbancTtl(notice.getRecrutPbancTtl())
                    .logoUrl(notice.getAgency() != null ? notice.getAgency().getLogoUrl() : null)
                    .isScarp(isScrap)
                    .build();
        });
    }
}
