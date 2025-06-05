package com.example.jobservice.domain.notice.converter;

import com.example.jobservice.domain.notice.dto.scrapNotice.NoticeScrapResDTO;
import com.example.jobservice.domain.notice.entity.Notice;
import com.example.jobservice.domain.notice.entity.NoticeScrap;

public class NoticeScrapConverter {
    public static NoticeScrap toNoticeScrap(Long userId, Notice notice) {
        return NoticeScrap.builder()
                .startDate(notice.getPbancBgngYmd())
                .endDate(notice.getPbancEndYmd())
                .userId(userId)
                .notice(notice)
                .build();
    }

    public static NoticeScrapResDTO.CalendarNoticeInformDTO toCalendarNoticeInformDTO(NoticeScrap noticeScrap) {
        return NoticeScrapResDTO.CalendarNoticeInformDTO.builder()
                .id(noticeScrap.getNotice().getId())
                .instNm(noticeScrap.getNotice().getInstNm())
                .pbancBgngYmd(noticeScrap.getStartDate())
                .pbancEndYmd(noticeScrap.getEndDate())
                .recrutPbancTtl(noticeScrap.getNotice().getRecrutPbancTtl())
                .build();
    }
}
