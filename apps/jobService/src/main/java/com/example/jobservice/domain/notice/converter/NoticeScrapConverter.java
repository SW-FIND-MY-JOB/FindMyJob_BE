package com.example.jobservice.domain.notice.converter;

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
}
