package com.example.jobservice.domain.notice.dto.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

public class NoticeResDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "채용 공고 응답 DTO")
    public static class NoticeInformDTO{
        @Schema(description = "공고 ID", example = "158715")
        Long id;

        @Schema(description = "기관명", example = "(사)남북교류협력지원협회")
        String instNm;

        @Schema(description = "기관유형명", example = "보건")
        String ncsCdNmLst;

        @Schema(description = "채용 유형명", example = "정규직")
        String hireTypeNmLst;

        @Schema(description = "근무지역", example = "서울")
        String workRgnNmLst;

        @Schema(description = "학력", example = "학력무관")
        String acbgCondNmLst;

        @Schema(description = "채용구분이름", example = "신입+경력")
        String recrutSeNm;

        @Schema(description = "공고마감일", example = "20250111")
        LocalDate pbancEndYmd;

        @Schema(description = "공고 타이틀", example = "채용 공고 제목입니다")
        String recrutPbancTtl;

        @Schema(description = "로고 이미지 주소", example = "http://~~")
        String logoUrl;

        @Schema(description = "조회수", example = "13")
        Long viewCnt;

        @Schema(description = "스크랩 유무", example = "true")
        Boolean isScrap;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "채용 공고 상세 응답 DTO")
    public static class NoticeDetailInformDTO{
        @Schema(description = "공고 ID", example = "158715")
        Long id;

        @Schema(description = "기관명", example = "(사)남북교류협력지원협회")
        String instNm;

        @Schema(description = "로고 이미지 주소", example = "http://~~")
        String logoUrl;

        @Schema(description = "기관유형명", example = "보건")
        String ncsCdNmLst;

        @Schema(description = "회사 설립일", example = "20001121")
        String establishmentAt;

        @Schema(description = "기관장", example = "김땡땡")
        String captain;

        @Schema(description = "회사 상세주소", example = "서울시 관악구 ~~")
        String address;

        @Schema(description = "채용 유형명", example = "정규직")
        String hireTypeNmLst;

        @Schema(description = "근무지역", example = "서울")
        String workRgnNmLst;

        @Schema(description = "학력", example = "학력무관")
        String acbgCondNmLst;

        @Schema(description = "채용구분이름", example = "경력직")
        String recruitSeNm;

        @Schema(description = "공고마감일", example = "20250111")
        LocalDate pbancEndYmd;

        @Schema(description = "공고 타이틀", example = "채용 공고 제목입니다")
        String recrutPbancTtl;

        @Schema(description = "우대조건", example = "우리의 우대조건은~~~")
        String prefCondCn;

        @Schema(description = "공고사이트 주소", example = "http://~~")
        String srcUrl;

        @Schema(description = "지원조건", example = "우리의 지원 조건은~~")
        String aplyQlfcCn;

        @Schema(description = "제한조건", example = "우리의 제한 조건은~~")
        String disqlfcRsn;

        @Schema(description = "채용절차", example = "우리의 채용 절차는~~")
        String scrnprcdrMthdExpln;

        @Schema(description = "지원방법", example = "우리의 지원 방법은~~")
        String nonatchRsn;

        @Schema(description = "조회수", example = "121")
        Long viewCnt;

        @Schema(description = "스크랩 유무", example = "true")
        Boolean isScrap;
    }
}
