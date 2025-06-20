package com.example.authservice.domain.point.exception.status;

import com.example.responselib.apiPayload.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PointErrorStatus implements BaseErrorCode {
    // 사용자 정보
    _NOT_ENOUGH_POINT(HttpStatus.CONFLICT, "USER4093", "포인트가 부족합니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
