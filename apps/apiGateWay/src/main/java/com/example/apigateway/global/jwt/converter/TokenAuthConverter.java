package com.example.apigateway.global.jwt.converter;

import com.example.apigateway.global.apiPayLoad.status.ErrorStatus;
import com.example.apigateway.global.exception.GeneralException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class TokenAuthConverter implements ServerAuthenticationConverter {

    private static final String BEARER = "Bearer ";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith(BEARER)) {
            String token = authHeader.substring(BEARER.length());
            return Mono.just(new UsernamePasswordAuthenticationToken(token, token));
        }

        //헤더에 토큰 없음 에러처리
        throw new GeneralException(ErrorStatus._NOT_EXIST_TOKEN);
    }
}
