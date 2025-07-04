package com.example.responselib.apiPayload.status;

import com.example.responselib.apiPayload.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 멤버 관려 에러
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),

    //내부 통신 에러
    _JOB_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "JOB503", "채용공고 서비스가 현재 응답하지 않습니다."),
    _COVER_LETTER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "COVERLETTER503", "자기소개서 서비스가 현재 응답하지 않습니다."),
    _AUTH_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "USER503", "사용자 서비스가 현재 응답하지 않습니다."),
    _CORRECTION_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "USER503", "AI 첨삭 서비스가 현재 응답하지 않습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}