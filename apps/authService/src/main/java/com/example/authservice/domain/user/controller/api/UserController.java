package com.example.authservice.domain.user.controller.api;

import com.example.authservice.domain.user.dto.UserReqDTO;
import com.example.authservice.domain.user.dto.UserResDTO;
import com.example.authservice.domain.user.exception.status.UserSuccessStatus;
import com.example.authservice.domain.user.service.UserService;
import com.example.responselib.apiPayload.ApiResponse;
import com.example.responselib.apiPayload.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

    //이메일 중복 체크
    @GetMapping("/mail")
    @Operation(summary = "이메일 중복 체크", description = "이메일 중복체크 하기")
    public ApiResponse<Boolean> checkMail(@RequestParam(required = false)@NotBlank String mail){
        Boolean isDuplicated = userService.isDuplicatedEmail(mail);
        return ApiResponse.of(SuccessStatus._OK, isDuplicated);
    }

    //로그인
    @PostMapping("/login")
    @Operation(summary = "로그인 하기", description = "로그인 하기")
    public ApiResponse<Null> login(@RequestBody @Valid UserReqDTO.LoginDTO loginDTO,
                                             HttpServletResponse response){
        userService.login(loginDTO, response);
        return ApiResponse.of(UserSuccessStatus._SUCCESS_LOGIN);
    }

    //로그아웃
    @PostMapping("/logout")
    @Operation(summary = "로그아웃 하기", description = "로그아웃 하기")
    public ApiResponse<Null> logout(HttpServletRequest request, HttpServletResponse response){
        userService.logout(request, response);

        return ApiResponse.of(UserSuccessStatus._SUCCESS_LOGOUT);
    }

    //사용자 정보 반환
    @GetMapping("/inform")
    @Operation(summary = "사용자 정보 가져오기", description = "사용자 정보 가져오기")
    public ApiResponse<UserResDTO.userInformDTO> getUserInform(HttpServletRequest request){
        UserResDTO.userInformDTO userInformDTO = userService.getUserInform(request);

        return ApiResponse.of(UserSuccessStatus._SUCCESS_GET_USER_INFORM, userInformDTO);
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
