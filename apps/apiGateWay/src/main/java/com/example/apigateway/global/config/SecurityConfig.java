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
                //api-gate-way
                "/health/**",
                "/actuator/**",

                //auth-service
                "/auth-service/v3/api-docs/**",
                "/auth-service/swagger-ui/**",
                "/auth-service/health/**",
                "/auth-service/api/users/login",
                "/auth-service/api/users/join",
                "/auth-service/api/users/mail",
                "/auth-service/api/mail/**",
                "/auth-service/api/token/reissue",

                //job-service
                "/job-service/v3/api-docs/**",
                "/job-service/swagger-ui/**",
                "/job-service/health/**",
                "/job-service/api/agency/**",
                "/job-service/api/notices/**",

                //cover-letter-service
                "/cover-letter-service/v3/api-docs/**",
                "/cover-letter-service/swagger-ui/**",
                "/cover-letter-service/health/**",

                //correction-service
                "/correction-service/v3/api-docs/**",
                "/correction-service/swagger-ui/**",
                "/correction-service/health/**"
        ));

        matchers.add(ServerWebExchangeMatchers.pathMatchers(
                //cover-letter-service
                HttpMethod.GET,
                "/cover-letter-service/api/cover-letters/**",
                "/cover-letter-service/api/cover-letter-rankings/**"
        ));

        matchers.add(ServerWebExchangeMatchers.pathMatchers(HttpMethod.OPTIONS, "/**"));

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
                        //api-gate-way 인가설정
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/health/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/actuator/**").permitAll()

                        //auth-service 인가설정
                        .pathMatchers("/auth-service/v3/api-docs/**", "/auth-service/swagger-ui/**").permitAll()
                        .pathMatchers("/auth-service/health/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/auth-service/api/users/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/auth-service/api/users/join").permitAll()
                        .pathMatchers(HttpMethod.GET, "/auth-service/api/users/mail").permitAll()
                        .pathMatchers("/auth-service/api/mail/**").permitAll()
                        .pathMatchers("/auth-service/api/token/reissue").permitAll()

                        //job-service 인가설정
                        .pathMatchers("/job-service/v3/api-docs/**", "/job-service/swagger-ui/**").permitAll()
                        .pathMatchers("/job-service/health/**").permitAll()
                        .pathMatchers("/job-service/api/agency/**").permitAll()
                        .pathMatchers("/job-service/api/notices/**").permitAll()

                        //cover-letter-service 인가설정
                        .pathMatchers("/cover-letter-service/v3/api-docs/**", "/cover-letter-service/swagger-ui/**").permitAll()
                        .pathMatchers("/cover-letter-service/health/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/cover-letter-service/api/cover-letters/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/cover-letter-service/api/cover-letter-rankings/**").permitAll()

                        //correction-service 인가설정
                        .pathMatchers("/correction-service/v3/api-docs/**", "/correction-service/swagger-ui/**").permitAll()
                        .pathMatchers("/correction-service/health/**").permitAll()

                        .anyExchange().authenticated()
                );

        return http.build();
    }
}
