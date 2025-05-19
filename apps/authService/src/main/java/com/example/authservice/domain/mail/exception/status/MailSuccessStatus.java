package com.example.authservice.domain.mail.exception.status;

import com.example.responselib.apiPayload.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MailSuccessStatus implements BaseCode {
    _SEND_CODE_SUCCESS(HttpStatus.CREATED, "MAIL2011", "인증번호가 전송되었습니다."),
    _VERIFY_CODE_SUCCESS(HttpStatus.OK, "MAIL2001", "인증에 성공하였습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
