package com.example.authservice.domain.user.service;

import com.example.authservice.domain.user.dto.UserReqDTO;
import com.example.authservice.domain.user.entity.User;
import com.example.authservice.domain.user.exception.UserExceptionHandler;
import com.example.authservice.domain.user.exception.status.UserErrorStatus;
import com.example.authservice.domain.user.repository.UserRepository;
import com.example.authservice.global.redis.RedisUtil;
import com.example.jwtutillib.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    //회원가입
    @Transactional
    public void join(UserReqDTO.JoinDTO joinDTO){
        //이메일 중복여부
        if (userRepository.existsByEmail(joinDTO.getEmail())){
            throw new UserExceptionHandler(UserErrorStatus._ALREADY_EXIST_EMAIL);
        }

        //인증된 메일 여부
        if (!redisUtil.existData("verify: " + joinDTO.getEmail()) || !redisUtil.getData("verify: " + joinDTO.getEmail()).equals("true")){
            throw new UserExceptionHandler(UserErrorStatus._NOT_VERIFY_EMAIL);
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
        String access = jwtUtil.createJwt("access", user.getId(), user.getEmail(), user.getName(), user.getRole(), accessExpiredMs);
        log.info("발급된 JWT: {}", access);
        String refresh = jwtUtil.createJwt("refresh", user.getId(), user.getEmail(), user.getName(), user.getRole(), refreshExpiredMs);

        //redis에 refresh토큰이 있으면 제거
        if (redisUtil.existData("refresh: "+email)){
            redisUtil.deleteData("refresh: "+email);
        }
        //redis에 refresh토큰 저장
        redisUtil.setData("refresh: "+email, refresh, refreshExpiredMs);

        //쿠키 생성
        Cookie cookie = createCookie("refresh", refresh);

        //응답 헤더에 토큰과 쿠키 삽입
        response.addHeader("Authorization", "Bearer " + access);
        response.addCookie(cookie);
    }

    //이메일 중복여부
    public void isDuplicatedEmail(String mail){
        //이메일 중복여부
        if (userRepository.existsByEmail(mail)){
            throw new UserExceptionHandler(UserErrorStatus._ALREADY_EXIST_EMAIL);
        }
    }

    //비번 변경
    @Transactional
    public void setPw(UserReqDTO.SetPwDTO setPwDTO, String token){
        //token 파싱
        token = token.replace("Bearer ", "");

        User user = userRepository.findById(jwtUtil.getUserId(token))
                .orElseThrow(() -> new UserExceptionHandler(UserErrorStatus._NOT_EXIST_USER));

        //비밀번호 일치 여부
        if (!bCryptPasswordEncoder.matches(setPwDTO.getPassword(), user.getPassword())){
            throw new UserExceptionHandler(UserErrorStatus._NOT_EQUAL_PASSWORD);
        }

        //새 비밀번호로 업데이트
        user.setPassword(bCryptPasswordEncoder.encode(setPwDTO.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UserReqDTO.DeleteUserDTO deleteUserDTO, String token){
        //token 파싱
        token = token.replace("Bearer ", "");

        User user = userRepository.findById(jwtUtil.getUserId(token))
                .orElseThrow(() -> new UserExceptionHandler(UserErrorStatus._NOT_EXIST_USER));

        //비밀번호 일치 여부
        if (!bCryptPasswordEncoder.matches(deleteUserDTO.getPassword(), user.getPassword())){
            throw new UserExceptionHandler(UserErrorStatus._NOT_EQUAL_PASSWORD);
        }

        userRepository.delete(user);
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
