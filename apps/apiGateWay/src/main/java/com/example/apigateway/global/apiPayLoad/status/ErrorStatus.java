package com.example.apigateway.global.apiPayLoad.status;

import com.example.apigateway.global.apiPayLoad.baseCode.BaseErrorCode;
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
    _SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "COMMON503", "서비스가 현재 응답하지 않습니다."),

    // 토큰 관련 에러
    _NOT_EXIST_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4011", "엑세스 토큰이 없습니다."),
    _EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4012", "엑세스 토큰이 만료되었습니다."),
    _NOT_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4013", "엑세스 토큰이 아닙니다."),

    // 내부 통신 관련 에러
    _JOB_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "JOB503", "채용공고 서비스가 현재 응답하지 않습니다."),
    _COVER_LETTER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "COVERLETTER503", "자기소개서 서비스가 현재 응답하지 않습니다."),
    _AUTH_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "USER503", "사용자 서비스가 현재 응답하지 않습니다."),
    _CORRECTION_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "USER503", "AI 첨삭 서비스가 현재 응답하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}