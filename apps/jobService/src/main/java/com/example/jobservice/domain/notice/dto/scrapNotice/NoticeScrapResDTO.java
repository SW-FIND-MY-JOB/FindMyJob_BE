package com.example.jobservice.domain.notice.dto.scrapNotice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class NoticeScrapResDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "캘린더 채용 공고 응답 DTO")
    public static class CalendarNoticeInformDTO{
        @Schema(description = "공고 ID", example = "158715")
        Long id;

        @Schema(description = "기관명", example = "(사)남북교류협력지원협회")
        String instNm;

        @Schema(description = "공고시작일", example = "20250110")
        LocalDate pbancBgngYmd;

        @Schema(description = "공고마감일", example = "20250111")
        LocalDate pbancEndYmd;

        @Schema(description = "공고 타이틀", example = "채용 공고 제목입니다")
        String recrutPbancTtl;
    }

}
