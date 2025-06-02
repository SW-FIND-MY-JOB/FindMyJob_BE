package com.example.correctionservice.global.exception.status;

import com.example.responselib.apiPayload.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorStatus implements BaseErrorCode {
    //401
    _NOT_EXIST_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4011", "잘못된 토큰입니다."),
    _EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4012", "토큰이 만료되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
