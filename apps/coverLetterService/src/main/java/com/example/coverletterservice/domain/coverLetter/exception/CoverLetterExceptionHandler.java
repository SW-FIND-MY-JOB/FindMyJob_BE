package com.example.coverletterservice.domain.coverLetter.exception;

import com.example.coverletterservice.global.exception.GeneralException;
import com.example.responselib.apiPayload.BaseErrorCode;

public class CoverLetterExceptionHandler extends GeneralException {

    public CoverLetterExceptionHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
