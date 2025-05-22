package com.example.authservice.global.jwt.controller;

import com.example.authservice.global.jwt.exception.status.ReissueSuccessStatus;
import com.example.authservice.global.jwt.service.ReissueService;
import com.example.responselib.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
@Tag(name = "리프레시 토큰 API입니다", description = "리프레시 토큰 관련 API입니다")
public class ReissueController {
    private final ReissueService reissueService;

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "토큰 재발급")
    public ApiResponse<Null> reissue(HttpServletRequest request, HttpServletResponse response){
        reissueService.reissueToken(request, response);

        return ApiResponse.of(ReissueSuccessStatus._SUCCESS_CREATE_TOKEN);
    }
}
