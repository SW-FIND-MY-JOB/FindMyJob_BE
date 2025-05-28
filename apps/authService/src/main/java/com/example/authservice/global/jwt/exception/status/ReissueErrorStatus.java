package com.example.authservice.global.jwt.exception.status;

import com.example.responselib.apiPayload.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReissueErrorStatus implements BaseErrorCode {
    _NOT_EXIST_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4001", "리프레시 토큰이 없습니다."),
    _EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4002", "리프레시 토큰이 만료되었습니다."),
    _NOT_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4003", "리프레시 토큰이 아닙니다."),
    _NOT_EXIST_TOKEN_IN_DB(HttpStatus.BAD_REQUEST, "TOKEN4004", "리프레시 토큰이 DB에 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
