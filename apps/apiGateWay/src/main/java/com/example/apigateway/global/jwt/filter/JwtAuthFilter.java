package com.example.apigateway.global.jwt.filter;

import com.example.apigateway.global.jwt.converter.TokenAuthConverter;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

public class JwtAuthFilter extends AuthenticationWebFilter {
    public JwtAuthFilter(ReactiveAuthenticationManager authenticationManager) {
        super(authenticationManager);
        setServerAuthenticationConverter(new TokenAuthConverter()); //여기서 헤더에서 토큰 추출
        setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));
    }
}
