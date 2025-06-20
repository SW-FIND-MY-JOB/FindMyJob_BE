package com.example.authservice.domain.point.exception.status;

import com.example.responselib.apiPayload.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PointSuccessStatus implements BaseCode {
    _SUCCESS_GET_USER_POINT(HttpStatus.OK, "POINT2001", "사용자 포인트 내역 조회 성공"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
