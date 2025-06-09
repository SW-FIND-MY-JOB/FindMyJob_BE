package com.example.authservice.domain.user.exception.status;

import com.example.responselib.apiPayload.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorStatus implements BaseErrorCode {
    // 사용자 정보
    _NOT_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "USER4001", "존재하지 않는 이메일입니다."),
    _NOT_EQUAL_PASSWORD(HttpStatus.BAD_REQUEST, "USER4002", "비밀번호가 일치하지 않습니다."),
    _NOT_EXIST_USER(HttpStatus.BAD_REQUEST, "USER4003", "존재하지 않는 사용자입니다."),

    _ALREADY_EXIST_EMAIL(HttpStatus.CONFLICT, "USER4091", "이미 사용중인 이메일입니다."),
    _ALREADY_EXIST_PASSWORD(HttpStatus.CONFLICT, "USER4092", "기존 비밀번호와 일치합니다."),

    _NOT_VERIFY_EMAIL(HttpStatus.BAD_REQUEST, "USER4004", "인증되지 않은 메일입니다."),

    _NOT_ENOUGH_POINT(HttpStatus.CONFLICT, "USER4093", "포인트가 부족합니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
