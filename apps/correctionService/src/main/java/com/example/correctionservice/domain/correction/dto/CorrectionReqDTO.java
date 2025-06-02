package com.example.correctionservice.domain.correction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CorrectionReqDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "자소서 첨삭 요청 DTO")
    public static class CorrectionReqInform{
        @Size(min = 5, max = 500)
        @Schema(description = "사용자가 작성한 자소서 제목", example = "지원동기 500자 내외")
        private String title;

        @Size(min = 100, max = 2000)
        @Schema(description = "사용자가 작성한 자소서 내용", example = "저는 유년 시절에~~")
        private String content;

        @NotNull
        @Schema(description = "다른 사람이 작성한 자소서 ID", example = "12")
        private Long coverLetterId;
    }
}