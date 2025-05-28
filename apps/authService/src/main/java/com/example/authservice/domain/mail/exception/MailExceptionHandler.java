package com.example.authservice.domain.mail.exception;

import com.example.authservice.global.exception.GeneralException;
import com.example.responselib.apiPayload.BaseErrorCode;

public class MailExceptionHandler extends GeneralException {

    public MailExceptionHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
