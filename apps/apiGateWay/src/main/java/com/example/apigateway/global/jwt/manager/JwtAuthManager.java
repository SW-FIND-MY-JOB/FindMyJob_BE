package com.example.apigateway.global.jwt.manager;

import com.example.apigateway.global.apiPayLoad.status.ErrorStatus;
import com.example.apigateway.global.exception.GeneralException;
import com.example.jwtutillib.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthManager implements ReactiveAuthenticationManager {
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        //토큰이 만료되었다면 에러처리
        try {
            jwtUtil.isExpired(token);
        } catch (Exception e) {
            log.error("토큰에러 {}", e.getMessage());
            throw new GeneralException(ErrorStatus._EXPIRED_TOKEN);
        }

        //토큰이 access 토큰인지 확인
        if (!jwtUtil.getCategory(token).equals("access")){
            log.error("토큰 access 체크");
            throw new GeneralException(ErrorStatus._NOT_ACCESS_TOKEN);
        }

        Long userId = jwtUtil.getUserId(token);
        String role = jwtUtil.getRole(token);

        return Mono.just(new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority(role))
        ));
    }
}
