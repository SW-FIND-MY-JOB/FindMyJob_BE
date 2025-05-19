package com.example.apigateway.global.apiPayLoad.baseCode;

import com.example.apigateway.global.apiPayLoad.dto.ErrorReasonDTO;
import org.springframework.http.HttpStatus;

public interface BaseCode {
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();

    default ErrorReasonDTO getReason(){
        return ErrorReasonDTO.builder()
                .message(getMessage())
                .code(getCode())
                .isSuccess(true)
                .build();
    }

    default ErrorReasonDTO getReasonHttpStatus(){
        return ErrorReasonDTO.builder()
                .message(getMessage())
                .code(getCode())
                .httpStatus(getHttpStatus())
                .isSuccess(true)
                .build();
    }
}

