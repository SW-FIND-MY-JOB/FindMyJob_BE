package com.example.apigateway.global.config;

import com.example.apigateway.global.jwt.filter.JwtAuthFilter;
import com.example.apigateway.global.jwt.manager.JwtAuthManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // 인가설정
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, JwtAuthManager authManager) {

        //커스텀 필터
        JwtAuthFilter jwtFilter = new JwtAuthFilter(authManager);

        List<ServerWebExchangeMatcher> matchers = new ArrayList<>();

        matchers.add(ServerWebExchangeMatchers.pathMatchers(
                //auth-service
                "/auth-service/health/**",
                "/auth-service/api/users/login",
                "/auth-service/api/users/join",
                "/auth-service/api/users/mail",
                "/auth-service/api/mail/**",
                "/auth-service/api/token/reissue",

                //job-service
                "/job-service/health/**",
                "/job-service/api/agency/**",
                "/job-service/api/notice/**",

                //cover-letter-service
                "/cover-letter-service/health/**"
        ));

        matchers.add(ServerWebExchangeMatchers.pathMatchers(
                //cover-letter-service
                HttpMethod.GET, "/cover-letter-service/api/cover-letters/**"
        ));

        ServerWebExchangeMatcher excludeLoginPaths = new OrServerWebExchangeMatcher(matchers);

        jwtFilter.setRequiresAuthenticationMatcher(
                new NegatedServerWebExchangeMatcher(excludeLoginPaths)
        );

        // 인증 정보 저장  x
        jwtFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        http
                .csrf(csrf -> csrf.disable())
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui/index.html").permitAll()

                        //auth-service 인가설정
                        .pathMatchers("/auth-service/v3/api-docs/**", "/auth-service/swagger-ui/**", "/auth-service/swagger-ui/index.html").permitAll()
                        .pathMatchers("/auth-service/health/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/auth-service/api/users/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/auth-service/api/users/join").permitAll()
                        .pathMatchers(HttpMethod.GET, "/auth-service/api/users/mail").permitAll()
                        .pathMatchers("/auth-service/api/mail/**").permitAll()
                        .pathMatchers("/auth-service/api/token/reissue").permitAll()

                        //job-service 인가설정
                        .pathMatchers("/job-service/health/**").permitAll()
                        .pathMatchers("/job-service/api/agency/**").permitAll()
                        .pathMatchers("/job-service/api/notice/**").permitAll()

                        //cover-letter-service 인가설정
                        .pathMatchers(HttpMethod.GET, "/cover-letter-service/api/cover-letters/**").permitAll()

                        .anyExchange().authenticated()
                );

        return http.build();
    }
}
