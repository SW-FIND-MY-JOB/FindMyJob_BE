package com.example.authservice.domain.mail.exception.status;

import com.example.responselib.apiPayload.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MailErrorStatus implements BaseErrorCode {
    _SEND_EMAIL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL5001", "이메일 전송 중 에러가 발생하였습니다."),
    _NOT_EXIST_CODE(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL5002", "서버측에 인증번호가 존재하지 않습니다."),
    _NOT_EQUAL_CODE(HttpStatus.BAD_REQUEST, "MAIL4004", "인증코드가 일치하지 않습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
