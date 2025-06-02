package com.example.coverletterservice.domain.coverLetter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CoverLetterReqDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "자기소개서 요청 DTO")
    public static class CoverLetterInformDTO {
        @Size(min = 2, max = 20, message = "기관명은 2자 이상 20자 이하로 입력해주세요.")
        @Schema(description = "기관명", example = "한국전력")
        String instNm;

        @Size(min = 2, max = 50, message = "직무 2자 이상 50자 이하로 입력해주세요.")
        @Schema(description = "직무", example = "보건")
        String ncsCdNmLst;

        @Size(min = 5, max = 500, message = "질문은 5자 이상 500자 이하로 입력해주세요.")
        @Schema(description = "질문", example = "지원동기 (300자 이상)")
        String title;

        @Size(min = 100, max = 2000, message = "내용은 100자 이상 2000자 이하로 입력해주세요.")
        @Schema(description = "내용", example = "기후변화로 인해 탄소중립이 전 세계....")
        String content;
    }
}
