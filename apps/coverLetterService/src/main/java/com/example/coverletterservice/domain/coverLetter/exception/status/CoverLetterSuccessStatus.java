package com.example.coverletterservice.domain.coverLetter.exception.status;

import com.example.responselib.apiPayload.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CoverLetterSuccessStatus implements BaseCode {
    //200
    _SUCCESS_GET_COVER_LETTER(HttpStatus.OK, "COVERLETTER2001", "자소서 조회 성공"),
    _SUCCESS_DELETE_COVER_LETTER(HttpStatus.OK, "COVERLETTER2002", "자소서 삭제 성공"),
    _SUCCESS_DELETE_SCRAP_COVER_LETTER(HttpStatus.OK, "COVERLETTER2003", "자소서 스크랩 해제 성공"),

    _SUCCESS_GET_WEEK_RANKING(HttpStatus.OK, "COVERLETTER2004", "주간 자소서 랭킹 조회 성공"),

    //201
    _SUCCESS_CREATE_COVER_LETTER(HttpStatus.OK, "COVERLETTER2011", "자소서 저장 성공"),
    _SUCCESS_SCRAP_COVER_LETTER(HttpStatus.OK, "COVERLETTER2012", "자소서 스크랩 성공"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
