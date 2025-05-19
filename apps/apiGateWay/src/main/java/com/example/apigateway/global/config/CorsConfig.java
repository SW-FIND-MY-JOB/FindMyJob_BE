package com.example.apigateway.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // 쿠키, 인증정보 포함 여부
        config.addAllowedOriginPattern("*"); // 모든 도메인 허용
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // GET, POST, PUT, DELETE 등 모든 메서드 허용
//        config.setAllowedOriginPatterns(List.of("https://your-frontend.com", "http://localhost:5173"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 위 CORS 적용

        return new CorsWebFilter(source);
    }
}
