package com.example.coverletterservice.domain.coverLetter.exception.status;

import com.example.responselib.apiPayload.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CoverLetterErrorStatus implements BaseErrorCode {
    //400
    _NOT_EXIST_COVER_LETTER(HttpStatus.BAD_REQUEST, "COVERLETTER4001", "해당 자소서 정보가 없습니다."),
    _NOT_EQUAL_USER_COVER_LETTER(HttpStatus.BAD_REQUEST, "COVERLETTER4002", "사용자와 작성자가 일치하지 않습니다."),
    _ALREADY_EXIST_COVER_LETTER_SCRAP(HttpStatus.BAD_REQUEST, "COVERLETTER4003", "이미 스크랩이 되어있습니다."),
    _NOT_EXIST_COVER_LETTER_SCRAP(HttpStatus.BAD_REQUEST, "COVERLETTER4004", "해당 스크랩 정보가 없습니다."),

    _NOT_EXIST_USER(HttpStatus.BAD_REQUEST, "USER4003", "존재하지 않는 사용자입니다."),

    //401
    _NOT_EQUAL_USER(HttpStatus.UNAUTHORIZED, "NOTICE4011", "스크랩한 사용자와 정보가 일치하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
