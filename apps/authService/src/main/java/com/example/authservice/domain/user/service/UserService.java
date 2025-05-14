package com.example.authservice.domain.user.service;

import com.example.authservice.domain.user.dto.UserReqDTO;
import com.example.authservice.domain.user.entity.User;
import com.example.authservice.domain.user.exception.UserExceptionHandler;
import com.example.authservice.domain.user.exception.status.UserErrorStatus;
import com.example.authservice.domain.user.repository.UserRepository;
import com.example.authservice.global.jwt.JWTUtil;
import com.example.authservice.global.redis.RedisUtil;
import com.example.responselib.apiPayload.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    //회원가입
    @Transactional
    public void join(UserReqDTO.JoinDTO joinDTO){
        //이메일 중복여부
        if (userRepository.existsByEmail(joinDTO.getEmail())){
            throw new UserExceptionHandler(UserErrorStatus._ALREADY_EXIST_EMAIL);
        }

        //사용자 생성
        User user = User.builder()
                .name(joinDTO.getName())
                .email(joinDTO.getEmail())
                .password(bCryptPasswordEncoder.encode(joinDTO.getPassword()))
                .role("USER")
                .build();

        userRepository.save(user);
    }

    // 로그인
    public void login(UserReqDTO.LoginDTO loginDTO, HttpServletResponse response){
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();

        //이메일 존재여부
        if (!userRepository.existsByEmail(email)){
            throw new UserExceptionHandler(UserErrorStatus._NOT_EXIST_EMAIL);
        }

        User user = userRepository.findByEmail(email);

        //비밀번호 일치 여부
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())){
            throw new UserExceptionHandler(UserErrorStatus._NOT_EQUAL_PASSWORD);
        }

        Long accessExpiredMs = 1000*60*30L;
        Long refreshExpiredMs = 1000*60*60*24L;

        //토큰 생성
        String access = jwtUtil.createJwt("access", user.getEmail(), user.getName(), user.getRole(), accessExpiredMs);
        String refresh = jwtUtil.createJwt("refresh", user.getEmail(), user.getName(), user.getRole(), refreshExpiredMs);

        //redis에 refresh토큰이 있으면 제거
        if (redisUtil.existData("refresh: "+email)){
            redisUtil.deleteData("refresh: "+email);
        }
        //redis에 refresh토큰 저장
        redisUtil.setData("refresh: "+email, refresh, refreshExpiredMs);

        //쿠키 생성
        Cookie cookie = createCookie("refresh", refresh);

        response.addHeader("Authorization", "Bearer " + access);
        response.addCookie(cookie);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 24 * 7);       // 7일
        cookie.setPath("/");                  // 모든 경로에 대해 전송
        cookie.setHttpOnly(true);             // JS에서 접근 불가 (보안)
        // cookie.setSecure(true);            // HTTPS만 허용 (배포 환경에서만 활성화)
        return cookie;
    }
}
