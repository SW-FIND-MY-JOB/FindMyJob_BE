package com.example.jobservice.domain.notice.exception.status;

import com.example.responselib.apiPayload.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NoticeSuccessStatus implements BaseCode {
    //200
    _SUCCESS_GET_NOTICE_INFORM(HttpStatus.OK, "NOTICE2001", "채용 공고 정보 가져오기 성공"),
    _SUCCESS_GET_NOTICE_SCRAP_INFORM(HttpStatus.OK, "NOTICE2002", "스크랩한 채용 공고 정보 가져오기 성공"),
    _SUCCESS_DELETE_NOTICE_SCRAP(HttpStatus.OK, "NOTICE2003", "공고 스크랩 해제"),

    //201
    _SUCCESS_POST_NOTICE_SCRAP(HttpStatus.OK, "NOTICE2003", "공고 스크랩 성공"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
