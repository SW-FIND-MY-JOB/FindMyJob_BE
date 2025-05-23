package com.example.jobservice.domain.agency.exception;

import com.example.jobservice.global.exception.GeneralException;
import com.example.responselib.apiPayload.BaseErrorCode;

public class AgencyExceptionHandler extends GeneralException {

    public AgencyExceptionHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
