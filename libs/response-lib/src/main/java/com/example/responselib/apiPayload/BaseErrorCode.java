package com.example.responselib.apiPayload;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();

    default ErrorReasonDTO getReason(){
        return ErrorReasonDTO.builder()
                .message(getMessage())
                .code(getCode())
                .isSuccess(false)
                .build();
    }

    default ErrorReasonDTO getReasonHttpStatus(){
        return ErrorReasonDTO.builder()
                .message(getMessage())
                .code(getCode())
                .httpStatus(getHttpStatus())
                .isSuccess(false)
                .build();
    }
}
