package com.example.jobservice.global.exception.status;

import com.example.responselib.apiPayload.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalSuccessStatus implements BaseCode {
    //200
    _SUCCESS_GET_NOTICE_INFORM(HttpStatus.OK, "NOTICE2001", "채용 공고 정보 가져오기 성공"),

    //201
    _SUCCESS_DELETE_USER(HttpStatus.OK, "USER2003", "사용자 삭제 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
