package com.example.authservice.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserReqDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "회원가입 요청 DTO")
    public static class JoinDTO{
        // 회원가입시 요청 필드
        @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해주세요.")
        @Schema(description = "이름", example = "김떙떙")
        String name;

        @Size(min = 6, max = 30, message = "이메일은 6자 이상 30자 이하로 입력해주세요.")
        @Schema(description = "이메일", example = "test@test.com")
        String email;

        @Size(min = 5, max = 15, message = "비밀번호는 5자 이상 15자 이하로 입력해주세요.")
        @Schema(description = "비밀번호", example = "qwerty1234")
        String password;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "로그인 요청 DTO")
    public static class LoginDTO{
        // 로그인시 요청 필드
        @Size(min = 6, max = 30, message = "이메일은 6자 이상 30자 이하로 입력해주세요.")
        @Schema(description = "이메일", example = "test@test.com")
        String email;

        @Size(min = 5, max = 15, message = "비밀번호는 5자 이상 15자 이하로 입력해주세요.")
        @Schema(description = "비밀번호", example = "qwerty1234")
        String password;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "비밀번호 변경 요청 DTO")
    public static class SetPwDTO{
        // 비밀번호 변경시 요청 필드
        @Size(min = 5, max = 15, message = "비밀번호는 5자 이상 15자 이하로 입력해주세요.")
        @Schema(description = "기존 비밀번호", example = "qwerty1234")
        String password;

        @Size(min = 5, max = 15, message = "비밀번호는 5자 이상 15자 이하로 입력해주세요.")
        @Schema(description = "새로운 비밀번호", example = "qwerty1234")
        String newPassword;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "회원탈퇴 요청 DTO")
    public static class DeleteUserDTO{
        @Size(min = 5, max = 15, message = "비밀번호는 5자 이상 15자 이하로 입력해주세요.")
        @Schema(description = "비밀번호", example = "qwerty1234")
        String password;
    }
}
