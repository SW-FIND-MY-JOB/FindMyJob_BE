package com.example.authservice.global.jwt.exception;

import com.example.authservice.global.exception.GeneralException;
import com.example.responselib.apiPayload.BaseErrorCode;

public class ReissueExceptionHandler extends GeneralException {

    public ReissueExceptionHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
