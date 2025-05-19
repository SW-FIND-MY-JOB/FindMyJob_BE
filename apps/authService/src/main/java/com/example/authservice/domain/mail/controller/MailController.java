package com.example.authservice.domain.mail.controller;

import com.example.authservice.domain.mail.dto.MailReqDTO;
import com.example.authservice.domain.mail.exception.status.MailSuccessStatus;
import com.example.authservice.domain.mail.service.MailService;
import com.example.responselib.apiPayload.ApiResponse;
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
public class MailController {
    private final MailService mailService;

    //회원가입 인증번호 발송
    @PostMapping("/auth-code")
    public ApiResponse<Null> sendAuthCode(@RequestBody MailReqDTO.MailDTO mailDTO) throws MessagingException {
        mailService.sendMail(mailDTO);

        return ApiResponse.of(MailSuccessStatus._SEND_CODE_SUCCESS);
    }

    //인증번호 검증
    @PostMapping("/verify-code")
    public ApiResponse<Null> verifyAuthCode(@RequestBody MailReqDTO.VerifyCodeDTO verifyCodeDTO){
        mailService.verifyEmailCode(verifyCodeDTO);

        return ApiResponse.of(MailSuccessStatus._VERIFY_CODE_SUCCESS);
    }
}
