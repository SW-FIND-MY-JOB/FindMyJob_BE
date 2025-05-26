package com.example.authservice.domain.user.controller;

import com.example.authservice.domain.user.dto.UserReqDTO;
import com.example.authservice.domain.user.exception.status.UserSuccessStatus;
import com.example.authservice.domain.user.service.UserService;
import com.example.responselib.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "사용자 관련 API입니다", description = "사용자 관련 API입니다")
public class UserController {
    private final UserService userService;

    //회원가입
    @PostMapping("/join")
    @Operation(summary = "회원가입 하기", description = "회원가입 하기")
    public ApiResponse<Null> join(@RequestBody @Valid UserReqDTO.JoinDTO joinDTO){
        userService.join(joinDTO);
        return ApiResponse.of(UserSuccessStatus._SUCCESS_JOIN);
    }

    //로그인
    @PostMapping("/login")
    @Operation(summary = "로그인 하기", description = "로그인 하기")
    public ApiResponse<Null> login(@RequestBody @Valid UserReqDTO.LoginDTO loginDTO,
                                             HttpServletResponse response){
        userService.login(loginDTO, response);
        return ApiResponse.of(UserSuccessStatus._SUCCESS_LOGIN);
    }

    //비밀번호 변경
    @PatchMapping("/pw")
    @Operation(summary = "비밀번호 변경하기", description = "비밀번호 변경하기")
    public ApiResponse<Null> setPw(@RequestBody @Valid UserReqDTO.SetPwDTO setPwDTO,
                                   @RequestHeader("Authorization") String token){
        userService.setPw(setPwDTO, token);
        return ApiResponse.of(UserSuccessStatus._SUCCESS_SET_PASSWORD);
    }

    //회웜 탈퇴
    @DeleteMapping("/delete")
    @Operation(summary = "회원 삭제하기", description = "회원 삭제하기")
    public ApiResponse<Null> deleteUser(@RequestBody @Valid UserReqDTO.DeleteUserDTO deleteUserDTO,
                                   @RequestHeader("Authorization") String token){
        userService.deleteUser(deleteUserDTO, token);
        return ApiResponse.of(UserSuccessStatus._SUCCESS_DELETE_USER);
    }
}
