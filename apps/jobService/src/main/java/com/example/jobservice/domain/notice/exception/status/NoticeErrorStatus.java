package com.example.jobservice.domain.notice.exception.status;

import com.example.responselib.apiPayload.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NoticeErrorStatus implements BaseErrorCode {
    //400
    _NOT_EXIST_NOTICE(HttpStatus.BAD_REQUEST, "NOTICE4001", "해당 공고가 없습니다."),
    _NOT_EXIST_NOTICE_SCRAP(HttpStatus.BAD_REQUEST, "NOTICE4002", "스크랩한 공고가 없습니다."),
    _ALREADY_EXIST_NOTICE_SCRAP(HttpStatus.BAD_REQUEST, "NOTICE4003", "이미 스크랩한 공고입니다."),

    //401
    _NOT_EQUAL_USER(HttpStatus.UNAUTHORIZED, "NOTICE4011", "스크랩한 사용자와 정보가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
