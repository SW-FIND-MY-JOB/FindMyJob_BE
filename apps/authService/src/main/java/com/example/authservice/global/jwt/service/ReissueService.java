package com.example.authservice.global.jwt.service;

import com.example.authservice.global.jwt.exception.ReissueExceptionHandler;
import com.example.authservice.global.jwt.exception.status.ReissueErrorStatus;
import com.example.authservice.global.redis.RedisUtil;
import com.example.jwtutillib.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReissueService {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Value("${config.isDev}")
    boolean isDev;

    public void reissueToken(HttpServletRequest request, HttpServletResponse response){
        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                    break;
                }
            }
        }

        //refresh토큰이 없다면 오류 메시지 반환
        if (refresh == null) {
            throw new ReissueExceptionHandler(ReissueErrorStatus._NOT_EXIST_TOKEN);
        }

        //refresh토큰이 만료되었다면 에러처리
        try {
            jwtUtil.isExpired(refresh);
        } catch (Exception e) {
            throw new ReissueExceptionHandler(ReissueErrorStatus._EXPIRED_TOKEN);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            //response status code
            throw new ReissueExceptionHandler(ReissueErrorStatus._NOT_REFRESH_TOKEN);
        }

        // 토큰이 redis에 있는지 확인
        if(!redisUtil.existData("refresh:" + jwtUtil.getEmail(refresh))){
            throw new ReissueExceptionHandler(ReissueErrorStatus._NOT_EXIST_TOKEN);
        }

        Long userId = jwtUtil.getUserId(refresh);
        String email = jwtUtil.getEmail(refresh);
        String name = jwtUtil.getName(refresh);
        String role = jwtUtil.getRole(refresh);

        Long accessExpiredMs = 1000*60*30L;
        Long refreshExpiredMs = 1000*60*60*24L;

        //토큰 생성
        String access = jwtUtil.createJwt("access", userId, email, name, role, accessExpiredMs);
        String newRefresh = jwtUtil.createJwt("refresh", userId, email, name, role, refreshExpiredMs);

        //redis에 refresh토큰이 있으면 제거
        if (redisUtil.existData("refresh:"+email)){
            redisUtil.deleteData("refresh:"+email);
        }

        //redis에 refresh토큰 저장
        redisUtil.setData("refresh:"+email, newRefresh, refreshExpiredMs);

        //쿠키 생성
        String cookieStr = createCookie("refresh", newRefresh, 60 * 60 * 24 * 7, isDev);

        //응답 헤더에 토큰과 쿠키 삽입
        response.addHeader("Authorization", "Bearer " + access);
        response.addHeader("Set-Cookie", cookieStr);
    }

    private String createCookie(String key, String value, int maxAge, boolean isDev) {
        StringBuilder cookieBuilder = new StringBuilder();
        cookieBuilder.append(key).append("=").append(value)
                .append("; Max-Age=").append(maxAge)
                .append("; Path=/")
                .append("; HttpOnly")
                .append("; SameSite=None");

        if (!isDev) {
            cookieBuilder.append("; Secure");
        }

        return cookieBuilder.toString();
    }
}
