package com.example.authservice.global.jwt.controller;

import com.example.authservice.global.jwt.exception.status.ReissueSuccessStatus;
import com.example.authservice.global.jwt.service.ReissueService;
import com.example.responselib.apiPayload.ApiResponse;
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
public class ReissueController {
    private final ReissueService reissueService;

    @PostMapping("/reissue")
    public ApiResponse<Null> reissue(HttpServletRequest request, HttpServletResponse response){
        reissueService.reissueToken(request, response);

        return ApiResponse.of(ReissueSuccessStatus._SUCCESS_CREATE_TOKEN);
    }
}
