package com.example.apigateway.global.exception;

import com.example.apigateway.global.apiPayLoad.baseCode.BaseErrorCode;
import com.example.apigateway.global.apiPayLoad.dto.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException{

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReason(){
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }
}