package com.example.jobservice.domain.notice.exception;

import com.example.jobservice.global.exception.GeneralException;
import com.example.responselib.apiPayload.BaseErrorCode;

public class NoticeExceptionHandler extends GeneralException {

    public NoticeExceptionHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
