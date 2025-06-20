package com.example.correctionservice.domain.correction.service;

import com.example.correctionservice.domain.correction.client.AuthServiceClient;
import com.example.correctionservice.global.exception.GeneralException;
import com.example.responselib.apiPayload.status.ErrorStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPointService {
    private final AuthServiceClient authServiceClient;

    @CircuitBreaker(name = "auth-service", fallbackMethod = "useUserPointFallback")
    public void useUserPoint(Long userId, int point, String description) {
        log.info("사용자 포인트 사용 요청: {}", userId);
        authServiceClient.useUserPoint(userId, point, description); // FeignClient 호출
    }

    // fallback 메서드
    public void useUserPointFallback(Long userId, int point, String description, Throwable t) {
        log.error("Circuit breaker fallback (useUserPointFallback) 작동! 원인: {}", t.getMessage());
        throw new GeneralException(ErrorStatus._AUTH_SERVICE_UNAVAILABLE);
    }

    @CircuitBreaker(name = "auth-service", fallbackMethod = "enoughUserPointFallback")
    public boolean enoughUserPoint(Long userId, int point) {
        log.info("사용자 포인트 사용 가능 여부 확인 요청: {}", userId);
        return authServiceClient.enoughUserPoint(userId, point); // FeignClient 호출
    }

    // fallback 메서드
    public boolean enoughUserPointFallback(Long userId, int point, Throwable t) {
        log.error("Circuit breaker fallback (enoughUserPointFallback) 작동! 원인: {}", t.getMessage());
        throw new GeneralException(ErrorStatus._AUTH_SERVICE_UNAVAILABLE);
    }

}
