package com.example.authservice.domain.mail.controller;

import com.example.authservice.domain.mail.dto.MailReqDTO;
import com.example.authservice.domain.mail.exception.status.MailSuccessStatus;
import com.example.authservice.domain.mail.service.MailService;
import com.example.responselib.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
@Tag(name = "메일 인증 관련 API입니다", description = "메일 인증 관련 API입니다")
public class MailController {
    private final MailService mailService;

    //회원가입 인증번호 발송
    @PostMapping("/auth-code")
    @Operation(summary = "회원가입 인증 발송", description = "회원가입 인증 발송")
    public ApiResponse<Null> sendAuthCode(@RequestBody MailReqDTO.MailDTO mailDTO) throws MessagingException {
        mailService.sendMail(mailDTO);

        return ApiResponse.of(MailSuccessStatus._SEND_CODE_SUCCESS);
    }

    //인증번호 검증
    @PostMapping("/verify-code")
    @Operation(summary = "인증번호 검증", description = "인증번호 검증")
    public ApiResponse<Null> verifyAuthCode(@RequestBody MailReqDTO.VerifyCodeDTO verifyCodeDTO){
        mailService.verifyEmailCode(verifyCodeDTO);

        return ApiResponse.of(MailSuccessStatus._VERIFY_CODE_SUCCESS);
    }
}
