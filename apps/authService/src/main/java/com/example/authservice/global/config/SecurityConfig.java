package com.example.authservice.global.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    //암호화를 시켜줌
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


    // API 보안 설정 (JWT, OAuth2 등)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //cors 설정
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
                        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);
                        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

                        return configuration;
                    }
                }));

        //csrf disable (jwt방식은 세션을 stateless상태로 관리하기 때문에 csrf에 대한 공격을 방어하지 않아도 된다.)
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식
        http
                .httpBasic((auth) -> auth.disable());

        //인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui/index.html").permitAll() // Swagger 관련 URL 허용
                        .requestMatchers("/api/login/**", "/").permitAll()
                        .requestMatchers("/health/**").permitAll()
                        .requestMatchers("/api/user/deleteUser").authenticated() // 인증 필요
//                        .requestMatchers("/api/user/deleteUser").hasRole("USER") // 인증 필요
                        .requestMatchers("/api/user/name").authenticated() // 인증 필요
                        .requestMatchers("/api/user/**").permitAll()
                        .requestMatchers("/api/email/**").permitAll() // email 인증 관련 URL 허용
                        .requestMatchers("/api/token/reissue").permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }
}
