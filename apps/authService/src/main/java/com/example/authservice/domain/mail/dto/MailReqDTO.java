package com.example.authservice.domain.mail.dto;

import lombok.Getter;

public class MailReqDTO {
    @Getter
    public static class MailDTO{
        String email;
    }

    @Getter
    public static class VerifyCodeDTO{
        String email;
        String code;
    }
}
