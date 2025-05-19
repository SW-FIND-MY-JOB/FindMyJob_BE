package com.example.apigateway.global.config;

import com.example.jwtutillib.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Value("${config.jwt-secret}")
    String jwtSecret;

    @Bean
    public JwtUtil jwtUtil(){
        return new JwtUtil(jwtSecret);
    };
}