package com.example.responselib.apiPayload.status;

import com.example.responselib.apiPayload.BaseCode;
import com.example.responselib.apiPayload.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    // 일반적인 응답
    _OK("COMMON200", "성공입니다.");

    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }
}
