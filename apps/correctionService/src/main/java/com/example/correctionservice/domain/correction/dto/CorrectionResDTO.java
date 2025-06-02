package com.example.correctionservice.domain.correction.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CorrectionResDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "AI 자소서 피드백 응답 DTO")
    public static class FeedbackResDTO{
        @Schema(description = "내용", example = "안정에서 나오는 원동력 한국전력의 재무 및 회계 업무 담당자로서 결산업무와 세무 업무를 전담하고 싶습니다.")
        private String content;

        @Schema(description = "좋은 점", example = "명확한 직무 목표를 설정하고 있으며, 지원자의 의도를 잘 나타내고 있습니다.")
        private String goodPoint;

        @Schema(description = "수정할 점", example = "문장이 다소 어색하게 연결되어 있어 읽기 쉽게 개선할 필요가 있습니다.")
        private String editPoint;

        @Schema(description = "수정한 내용", example = "안정적인 환경에서 나오는 원동력으로, 한국전력의 재무 및 회계 업무 담당자로서 결산업무와 세무 업무를 전담하고 싶습니다.")
        private String editContent;
    }
}
