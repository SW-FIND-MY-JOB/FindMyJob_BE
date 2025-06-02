package com.example.correctionservice.domain.correction.exception.status;

import com.example.responselib.apiPayload.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CorrectionSuccessStatus implements BaseCode {
    //200
    _SUCCESS_AI_CORRECTION_RESPONSE(HttpStatus.OK, "CORRECTION2001", "자소서 피드백 응답 성공"),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
