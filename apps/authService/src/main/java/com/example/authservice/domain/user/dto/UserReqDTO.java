package com.example.authservice.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserReqDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinDTO{
        // 회원가입시 요청 필드
        String name;
        String email;
        String password;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDTO{
        // 로그인시 요청 필드
        String email;
        String password;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SetpwDTO{
        // 비밀번호 변경시 요청 필드
        String password;
        String newPassword;
    }
}
