package com.example.authservice.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class UserResDTO {

    @Getter
    @Builder
    @Schema(description = "사용자 정보 응답 DTO")
    public static class userInformDTO{
        @Schema(description = "이름", example = "김떙떙")
        String name;

        @Schema(description = "포인트", example = "1000")
        int point;
    }
}
