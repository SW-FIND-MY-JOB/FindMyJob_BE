package com.example.authservice.global.exception.status;

import com.example.responselib.apiPayload.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalSuccessStatus implements BaseCode {
    //200

    //201
    _SUCCESS_DELETE_USER(HttpStatus.OK, "USER2003", "사용자 삭제 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
