package com.example.correctionservice.domain.correction.exception.status;

import com.example.responselib.apiPayload.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CorrectionErrorStatus implements BaseErrorCode {
    //400
    _NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "USER4005", "포인트가 부족합니다."),
    _NOT_EXIST_COVER_LETTER(HttpStatus.BAD_REQUEST, "COVERLETTER4001", "해당 자소서 정보가 없습니다."),

    //500
    _GPT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GPT5001", "GPT 응답 중 에러가 발생하였습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
