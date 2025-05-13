package com.example.responselib.apiPayload;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReasonDTO {
    private final boolean isSuccess;
    private final String code;
    private final String message;

    public boolean getIsSuccess(){
        return isSuccess;
    }
}