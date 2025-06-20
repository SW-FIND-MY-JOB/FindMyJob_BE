package com.example.authservice.domain.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class PointResDTO {
    @Getter
    @Builder
    @Schema(description = "포인트 정보 응답 DTO")
    public static class pointInformDTO{
        @Schema(description = "포인트 정보 아이디", example = "1")
        long id;

        @Schema(description = "포인트 적립(true) / 포인트 사용(false)", example = "true")
        private Boolean isAddPoint;

        @Schema(description = "적립포인트", example = "300")
        int updatePoint;

        @Schema(description = "잔여 포인트", example = "500")
        int balance;

        @Schema(description = "포인트 사용 메시지", example = "자기소개서 작성 리워드 지급")
        String description;

        @Schema(description = "생성일", example = "2025-06-20")
        LocalDateTime createdAt;
    }
}
