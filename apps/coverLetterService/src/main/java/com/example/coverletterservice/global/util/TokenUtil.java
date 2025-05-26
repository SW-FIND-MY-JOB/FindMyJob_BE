package com.example.coverletterservice.global.util;

import com.example.coverletterservice.global.exception.GeneralException;
import com.example.coverletterservice.global.exception.status.GlobalErrorStatus;
import com.example.jwtutillib.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TokenUtil {
    private final JwtUtil jwtUtil;

    //토큰 검사하는 로직
    public String checkToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        //토큰 약식 검증
        if (token == null || !token.startsWith("Bearer ")) {
            throw new GeneralException(GlobalErrorStatus._NOT_EXIST_TOKEN);
        }

        //Bearer 삭제
        token = token.substring(7);

        //토큰 만료 검사
        if(jwtUtil.isExpired(token)){
            throw new GeneralException(GlobalErrorStatus._EXPIRED_TOKEN);
        }

        return token;
    }

    //user id 가져오는 로직
    public Long getUserId(HttpServletRequest request){
        //사용자 정보 추출
        String token = request.getHeader("Authorization");

        // 토큰 검증
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            try {
                if (!jwtUtil.isExpired(token)) {
                    //user id 반환
                    return jwtUtil.getUserId(token);
                }
            }  catch (Exception e) {
                log.error("JWT 에러: {}", e.getMessage());
            }
        }

        return null;
    }
}
