package com.example.authservice.domain.user.controller;

import com.example.authservice.domain.user.dto.UserReqDTO;
import com.example.authservice.domain.user.exception.status.UserSuccessStatus;
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
        return ApiResponse.of(UserSuccessStatus._SUCCESS_JOIN);
    }

    //로그인
    @PostMapping("/login")
    public ApiResponse<Null> login(@RequestBody UserReqDTO.LoginDTO loginDTO,
                                             HttpServletResponse response){
        userService.login(loginDTO, response);
        return ApiResponse.of(UserSuccessStatus._SUCCESS_LOGIN);
    }

    //비밀번호 변경
    @PatchMapping("/pw")
    public ApiResponse<Null> setPw(@RequestBody UserReqDTO.SetPwDTO setPwDTO,
                                   @RequestHeader("Authorization") String token){
        userService.setPw(setPwDTO, token);
        return ApiResponse.of(UserSuccessStatus._SUCCESS_SET_PASSWORD);
    }

    //회웜 탈퇴
    @DeleteMapping("/delete")
    public ApiResponse<Null> deleteUser(@RequestBody UserReqDTO.DeleteUserDTO deleteUserDTO,
                                   @RequestHeader("Authorization") String token){
        userService.deleteUser(deleteUserDTO, token);
        return ApiResponse.of(UserSuccessStatus._SUCCESS_DELETE_USER);
    }
}
