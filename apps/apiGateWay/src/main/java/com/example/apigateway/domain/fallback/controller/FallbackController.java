package com.example.apigateway.domain.fallback.controller;

import com.example.apigateway.global.apiPayLoad.ApiResponse;
import com.example.apigateway.global.apiPayLoad.status.ErrorStatus;
import jakarta.validation.constraints.Null;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/auth")
    public ResponseEntity<ApiResponse<Null>> authFallback(){
        log.error("사용자 서비스가 현재 응답하지 않습니다.");
        return ResponseEntity
                .status(ErrorStatus._AUTH_SERVICE_UNAVAILABLE.getHttpStatus())
                .body(ApiResponse.onFailure(ErrorStatus._AUTH_SERVICE_UNAVAILABLE, null));
    }

    @RequestMapping("/job")
    public ResponseEntity<ApiResponse<Null>> jobFallback(){
        log.error("채용공고 서비스가 현재 응답하지 않습니다.");
        return ResponseEntity
                .status(ErrorStatus._AUTH_SERVICE_UNAVAILABLE.getHttpStatus())
                .body(ApiResponse.onFailure(ErrorStatus._JOB_SERVICE_UNAVAILABLE, null));
    }

    @RequestMapping("/cover-letter")
    public ResponseEntity<ApiResponse<Null>> coverLetterFallback(){
        log.error("자소서 서비스가 현재 응답하지 않습니다.");
        return ResponseEntity
                .status(ErrorStatus._AUTH_SERVICE_UNAVAILABLE.getHttpStatus())
                .body(ApiResponse.onFailure(ErrorStatus._COVER_LETTER_SERVICE_UNAVAILABLE, null));
    }

    @RequestMapping("/correction")
    public ResponseEntity<ApiResponse<Null>> correctionFallback(){
        log.error("첨삭 서비스가 현재 응답하지 않습니다.");
        return ResponseEntity
                .status(ErrorStatus._AUTH_SERVICE_UNAVAILABLE.getHttpStatus())
                .body(ApiResponse.onFailure(ErrorStatus._CORRECTION_SERVICE_UNAVAILABLE, null));
    }
}
