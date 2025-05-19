package com.example.authservice.domain.user.exception.status;

import com.example.responselib.apiPayload.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserSuccessStatus implements BaseCode {
    _SUCCESS_JOIN(HttpStatus.CREATED, "USER2011", "회원가입 성공"),
    _SUCCESS_LOGIN(HttpStatus.OK, "USER2001", "로그인 성공"),
    _SUCCESS_SET_PASSWORD(HttpStatus.OK, "USER2002", "비밀번호 변경 성공"),
    _SUCCESS_DELETE_USER(HttpStatus.OK, "USER2003", "사용자 삭제 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
