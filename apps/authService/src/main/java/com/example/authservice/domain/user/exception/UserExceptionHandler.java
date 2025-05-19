package com.example.authservice.domain.user.exception;

import com.example.authservice.global.exception.GeneralException;
import com.example.responselib.apiPayload.BaseErrorCode;

public class UserExceptionHandler extends GeneralException {

    public UserExceptionHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
