package com.example.jobservice.domain.notice.converter;

import com.example.jobservice.domain.notice.dto.notice.NoticeResDTO;
import com.example.jobservice.domain.notice.entity.Notice;

public class NoticeConverter {
    public static NoticeResDTO.NoticeInformDTO toNoticeResDTO(Notice notice) {
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
                .isScarp(false)
                .build();
    }
}
