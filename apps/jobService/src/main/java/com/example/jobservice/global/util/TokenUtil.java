package com.example.jobservice.global.util;

import com.example.jobservice.global.exception.GeneralException;
import com.example.jobservice.global.exception.status.GlobalErrorStatus;
import com.example.jwtutillib.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

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
}
