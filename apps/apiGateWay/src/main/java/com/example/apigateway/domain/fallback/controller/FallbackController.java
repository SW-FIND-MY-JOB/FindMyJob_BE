package com.example.apigateway.domain.fallback.controller;

import com.example.apigateway.global.apiPayLoad.ApiResponse;
import com.example.apigateway.global.apiPayLoad.status.ErrorStatus;
import jakarta.validation.constraints.Null;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/auth")
    public ApiResponse<Null> authFallback(){
        return ApiResponse.onFailure(ErrorStatus._SERVICE_UNAVAILABLE, null);
    }
}
