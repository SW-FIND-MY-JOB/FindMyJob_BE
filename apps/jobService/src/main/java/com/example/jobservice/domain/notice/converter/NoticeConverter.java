package com.example.jobservice.domain.notice.converter;

import com.example.jobservice.domain.agency.entity.Agency;
import com.example.jobservice.domain.notice.dto.notice.NoticeApiResponseDTO;
import com.example.jobservice.domain.notice.dto.notice.NoticeResDTO;
import com.example.jobservice.domain.notice.entity.Notice;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class NoticeConverter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static Notice toNotice(NoticeApiResponseDTO.NoticePostingDTO dto, Agency agency) {
        return Notice.builder()
                .id(dto.getRecrutPblntSn())
                .instNm(dto.getInstNm())
                .ncsCdNmLst(dto.getNcsCdNmLst())
                .ncsCdLst(dto.getNcsCdLst())
                .hireTypeNmLst(dto.getHireTypeNmLst())
                .hireTypeLst(dto.getHireTypeLst())
                .workRgnNmLst(dto.getWorkRgnNmLst())
                .workRgnLst(dto.getWorkRgnLst())
                .recrutSeNm(dto.getRecrutSeNm())
                .recrutSe(dto.getRecrutSe())
                .prefCondCn(dto.getPrefCondCn())
                .pbancBgngYmd(LocalDate.parse(dto.getPbancBgngYmd(), formatter))
                .pbancEndYmd(LocalDate.parse(dto.getPbancEndYmd(), formatter))
                .recrutPbancTtl(dto.getRecrutPbancTtl())
                .srcUrl(dto.getSrcUrl())
                .aplyQlfcCn(dto.getAplyQlfcCn())
                .disqlfcRsn(dto.getDisqlfcRsn())
                .scrnprcdrMthdExpln(dto.getScrnprcdrMthdExpln())
                .acbgCondNmLst(dto.getAcbgCondNmLst())
                .acbgCondLst(dto.getAcbgCondLst())
                .nonatchRsn(dto.getNonatchRsn())
                .viewCnt(0L)
                .agency(agency)
                .build();
    }

    public static NoticeResDTO.NoticeInformDTO toNoticeResDTO(Notice notice, boolean isScrap) {
        return NoticeResDTO.NoticeInformDTO.builder()
                .id(notice.getId())
                .instNm(notice.getInstNm())
                .ncsCdNmLst(notice.getNcsCdNmLst())
                .hireTypeNmLst(notice.getHireTypeNmLst())
                .workRgnNmLst(notice.getWorkRgnNmLst())
                .acbgCondNmLst(notice.getAcbgCondNmLst())
                .recrutSeNm(notice.getRecrutSeNm())
                .pbancEndYmd(notice.getPbancEndYmd())
                .recrutPbancTtl(notice.getRecrutPbancTtl())
                .logoUrl(notice.getAgency() != null ? notice.getAgency().getLogoUrl() : null)
                .viewCnt(notice.getViewCnt())
                .isScrap(isScrap)
                .build();
    }

    // 채용 공고 상세 응답
    public static NoticeResDTO.NoticeDetailInformDTO toNoticeDetailResDTO(Notice notice, boolean isScrap) {
        return NoticeResDTO.NoticeDetailInformDTO.builder()
                .id(notice.getId())
                .instNm(notice.getInstNm())
                .logoUrl(notice.getAgency() != null ? notice.getAgency().getLogoUrl() : null)
                .ncsCdNmLst(notice.getNcsCdNmLst())
                .establishmentAt(notice.getAgency() != null ? notice.getAgency().getEstablishmentAt() : null)
                .captain(notice.getAgency() != null ? notice.getAgency().getCaptain() : null)
                .address(notice.getAgency() != null ? notice.getAgency().getAddress() : null)
                .hireTypeNmLst(notice.getHireTypeNmLst())
                .workRgnNmLst(notice.getWorkRgnNmLst())
                .acbgCondNmLst(notice.getAcbgCondNmLst())
                .recruitSeNm(notice.getRecrutSeNm())
                .pbancBgngYmd(notice.getPbancBgngYmd())
                .pbancEndYmd(notice.getPbancEndYmd())
                .recrutPbancTtl(notice.getRecrutPbancTtl())
                .prefCondCn(notice.getPrefCondCn())
                .srcUrl(notice.getSrcUrl())
                .aplyQlfcCn(notice.getAplyQlfcCn())
                .disqlfcRsn(notice.getDisqlfcRsn())
                .scrnprcdrMthdExpln(notice.getScrnprcdrMthdExpln())
                .nonatchRsn(notice.getNonatchRsn())
                .viewCnt(notice.getViewCnt())
                .isScrap(isScrap)
                .build();
    }
}
