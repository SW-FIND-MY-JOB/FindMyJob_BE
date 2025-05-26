package com.example.coverletterservice.domain.coverLetter.converter;

import com.example.coverletterservice.domain.coverLetter.dto.CoverLetterReqDTO;
import com.example.coverletterservice.domain.coverLetter.dto.CoverLetterResDTO;
import com.example.coverletterservice.domain.coverLetter.entity.CoverLetter;

import java.util.List;

public class CoverLetterConverter {

    //자소서 객체로 변환
    public static CoverLetter toCoverLetter(CoverLetterReqDTO.CoverLetterInformDTO dto, Long userId){
        return CoverLetter.builder()
                .userId(userId)
                .instNm(dto.getInstNm())
                .ncsCdNmLst(dto.getNcsCdNmLst())
                .title(dto.getTitle())
                .content(dto.getContent())
                .viewCnt(0L)
                .build();
    }

    //최신 자소서dto로 변환
    public static CoverLetterResDTO.CoverLetterRecentInformDTO toCoverLetterRecentInformDTO(CoverLetter coverLetter){
        return CoverLetterResDTO.CoverLetterRecentInformDTO.builder()
                .id(coverLetter.getId())
                .instNm(coverLetter.getInstNm())
                .ncsCdNmLst(coverLetter.getNcsCdNmLst())
                .title(coverLetter.getTitle())
                .content(coverLetter.getContent())
                .build();
    }

    //단일 자소서 dto로 변환
    public static CoverLetterResDTO.CoverLetterDetailInformDTO toCoverLetterDetailInformDTO(
            CoverLetter coverLetter,
            Boolean isAuthor,
            Boolean isScrap,
            List<CoverLetterResDTO.CoverLetterRecentInformDTO> coverLetterRecentInformDTOList){
        return CoverLetterResDTO.CoverLetterDetailInformDTO.builder()
                .id(coverLetter.getId())
                .isAuthor(isAuthor)
                .isScrap(isScrap)
                .viewCnt(coverLetter.getViewCnt())
                .instNm(coverLetter.getInstNm())
                .ncsCdNmLst(coverLetter.getNcsCdNmLst())
                .title(coverLetter.getTitle())
                .content(coverLetter.getContent())
                .createAt(coverLetter.getCreatedAt())
                .recentInformList(coverLetterRecentInformDTOList)
                .build();
    }

    // 자소서 dto로 변환
    public static CoverLetterResDTO.CoverLetterInformDTO toCoverLetterInformDTO (
            CoverLetter coverLetter,
            Boolean isScrap){
        return CoverLetterResDTO.CoverLetterInformDTO.builder()
                .id(coverLetter.getId())
                .instNm(coverLetter.getInstNm())
                .ncsCdNmLst(coverLetter.getNcsCdNmLst())
                .title(coverLetter.getTitle())
                .content(coverLetter.getContent())
                .viewCnt(coverLetter.getViewCnt())
                .isScrap(isScrap)
                .build();
    }
}
