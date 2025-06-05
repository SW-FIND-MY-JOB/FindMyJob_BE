package com.example.jobservice.domain.notice.service;

import com.example.jobservice.domain.notice.converter.NoticeConverter;
import com.example.jobservice.domain.notice.converter.NoticeScrapConverter;
import com.example.jobservice.domain.notice.dto.notice.NoticeResDTO;
import com.example.jobservice.domain.notice.dto.scrapNotice.NoticeScrapResDTO;
import com.example.jobservice.domain.notice.entity.Notice;
import com.example.jobservice.domain.notice.entity.NoticeScrap;
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

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeScrapService {
    private final NoticeScrapRepository noticeScrapRepository;
    private final NoticeRepository noticeRepository;
    private final TokenUtil tokenUtil;
    private final JwtUtil jwtUtil;

    //공고 스크랩 저장
    @Transactional
    public void saveNoticeScrap(HttpServletRequest request, Long noticeId) {
        //토큰 검증
        String token = tokenUtil.checkToken(request);

        //사용자 ID 추출
        Long userId = jwtUtil.getUserId(token);

        //공고 정보 추출
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new GeneralException(NoticeErrorStatus._NOT_EXIST_NOTICE));

        //이미 스크랩 해둔 공고인지 확인
        if(noticeScrapRepository.existsByNoticeAndUserId(notice, userId)) {
            throw new GeneralException(NoticeErrorStatus._ALREADY_EXIST_NOTICE_SCRAP);
        }

        //entity 생성
        NoticeScrap noticeScrap = NoticeScrapConverter.toNoticeScrap(userId, notice);

        noticeScrapRepository.save(noticeScrap);
    }

    //공고 스크랩 해제
    @Transactional
    public void deleteNoticeScrap(HttpServletRequest request, Long noticeId) {
        //토큰 검증
        String token = tokenUtil.checkToken(request);

        //사용자 ID 추출
        Long userId = jwtUtil.getUserId(token);

        //공고 가져오기
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new GeneralException(NoticeErrorStatus._NOT_EXIST_NOTICE));

        //해당 공고 스크랩 정보 가져오기
        NoticeScrap noticeScrap = noticeScrapRepository.findByNoticeAndUserId(notice, userId)
                .orElseThrow(() -> new GeneralException(NoticeErrorStatus._NOT_EXIST_NOTICE_SCRAP));

        //삭제
        noticeScrapRepository.delete(noticeScrap);
    }

    //사용자 id에 따른 스크랩 내역 삭제
    @Transactional
    public void deleteUserNoticeScraps(Long userId){
        noticeScrapRepository.deleteAllByUserId(userId);
        log.info("해당 사용자의 공고 스크랩이 삭제되었습니다.");
    }

    //스크랩 공고 보여주기
    public Page<NoticeResDTO.NoticeInformDTO> searchNoticeScrap(HttpServletRequest request, int page, int size) {
        //토큰 검증
        String token = tokenUtil.checkToken(request);

        //사용자 ID 추출
        Long userId = jwtUtil.getUserId(token);

        //사용자 ID에 맞는 스크랩 공고 가져오기
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<NoticeScrap> result = noticeScrapRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);

        return result.map(dto-> NoticeConverter.toNoticeResDTO(dto.getNotice(), true));
    }

    //월별 스크랩 공고 보여주기
    public List<NoticeScrapResDTO.CalendarNoticeInformDTO> searchDateNoticeScrap(HttpServletRequest request, int year, int month) {
        //토큰 검증
        String token = tokenUtil.checkToken(request);

        //사용자 ID 추출
        Long userId = jwtUtil.getUserId(token);

        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        //사용자 ID와 date에 맞는 스크랩 공고 가져오기
        List<NoticeScrap> result = noticeScrapRepository.findNoticesWithinMonth(userId, startOfMonth, endOfMonth);

        return result.stream().map(NoticeScrapConverter::toCalendarNoticeInformDTO).toList();
    }
}
