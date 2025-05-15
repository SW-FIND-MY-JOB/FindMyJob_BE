package com.example.authservice.global.jwt.exception.status;

import com.example.responselib.apiPayload.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReissueSuccessStatus implements BaseCode {
    _SUCCESS_CREATE_TOKEN(HttpStatus.CREATED, "MAIL2011", "토큰 재발급 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
