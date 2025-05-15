package com.example.authservice.global.jwt.service;

import com.example.authservice.global.jwt.JwtUtil;
import com.example.authservice.global.jwt.exception.ReissueExceptionHandler;
import com.example.authservice.global.jwt.exception.status.ReissueErrorStatus;
import com.example.authservice.global.redis.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReissueService {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    public void reissueToken(HttpServletRequest request, HttpServletResponse response){
        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
                log.info("refresh토큰 {}", refresh);
            }
        }

        //refresh토큰이 없다면 오류 메시지 반환
        if (refresh == null) {
            throw new ReissueExceptionHandler(ReissueErrorStatus._NOT_EXIST_TOKEN);
        }

        //refresh토큰이 만료되었다면 에러처리
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new ReissueExceptionHandler(ReissueErrorStatus._EXPIRED_TOKEN);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            //response status code
            throw new ReissueExceptionHandler(ReissueErrorStatus._NOT_REFRESH_TOKEN);
        }

        // 토큰이 redis에 있는지 확인
        if(!redisUtil.existData("refresh: " + jwtUtil.getEmail(refresh))){
            throw new ReissueExceptionHandler(ReissueErrorStatus._NOT_EXIST_TOKEN);
        }

        String email = jwtUtil.getEmail(refresh);
        String name = jwtUtil.getName(refresh);
        String role = jwtUtil.getRole(refresh);

        Long accessExpiredMs = 1000*60*30L;
        Long refreshExpiredMs = 1000*60*60*24L;

        //토큰 생성
        String access = jwtUtil.createJwt("access", email, name, role, accessExpiredMs);
        String newRefresh = jwtUtil.createJwt("refresh", email, name, role, refreshExpiredMs);

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

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 24 * 7);       // 7일
        cookie.setPath("/");                  // 모든 경로에 대해 전송
        cookie.setHttpOnly(true);             // JS에서 접근 불가 (보안)
        // cookie.setSecure(true);            // HTTPS만 허용 (배포 환경에서만 활성화)
        return cookie;
    }
}
