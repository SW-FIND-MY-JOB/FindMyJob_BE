package com.example.authservice.domain.user.controller;

import com.example.authservice.domain.user.dto.UserReqDTO;
import com.example.authservice.domain.user.service.UserService;
import com.example.responselib.apiPayload.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    //회원가입
    @PostMapping("/join")
    public ApiResponse<Null> join(@RequestBody UserReqDTO.JoinDTO joinDTO){
        userService.join(joinDTO);
        return ApiResponse.onSuccess();
    }

    //로그인
    @PostMapping("/login")
    public ApiResponse<Null> login(@RequestBody UserReqDTO.LoginDTO loginDTO,
                                             HttpServletResponse response){
        userService.login(loginDTO, response);
        return ApiResponse.onSuccess();
    }
}
