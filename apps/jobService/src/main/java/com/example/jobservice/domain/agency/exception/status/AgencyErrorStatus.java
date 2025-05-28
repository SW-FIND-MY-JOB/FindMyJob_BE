package com.example.jobservice.domain.agency.exception.status;

import com.example.responselib.apiPayload.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AgencyErrorStatus implements BaseErrorCode {
    //400
    _NOT_EXIST_AGENCY(HttpStatus.BAD_REQUEST, "AGENCY4001", "저장된 기관 정보가 없습니다."),


    _NOT_VERIFY_EMAIL(HttpStatus.BAD_REQUEST, "USER4004", "인증되지 않은 메일입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
