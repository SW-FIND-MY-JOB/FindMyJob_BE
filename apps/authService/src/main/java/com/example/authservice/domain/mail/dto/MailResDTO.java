package com.example.authservice.domain.mail.dto;

import lombok.Getter;

public class MailResDTO {
    @Getter
    public static class MailVerifyDTO{
        String code;
    }
}
