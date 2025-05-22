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

        @Schema(description = "기관유형명", example = "준공공기관")
        String ncsCdNmLst;

        @Schema(description = "채용 유형명", example = "정규직")
        String hireTypeNmLst;

        @Schema(description = "근무지역", example = "서울")
        String workRgnNmLst;

        @Schema(description = "채용구분이름", example = "경력직")
        String recruitSeNm;

        @Schema(description = "공고마감일", example = "20250111")
        LocalDate pbancEndYmd;

        @Schema(description = "공고 타이틀", example = "채용 공고 제목입니다")
        String recrutPbancTtl;

        @Schema(description = "로고 이미지 주소", example = "http://~~")
        String logoUrl;

        @Schema(description = "스크랩 유무", example = "TRUE / FALSE")
        Boolean isScarp;
    }
}
