package com.example.correctionservice.domain.correction.fallbackService;

import com.example.correctionservice.domain.correction.client.AuthServiceClient;
import com.example.correctionservice.domain.correction.exception.status.CorrectionErrorStatus;
import com.example.correctionservice.global.exception.GeneralException;
import com.example.responselib.apiPayload.status.ErrorStatus;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthFallbackService {
    private final AuthServiceClient authServiceClient;

    @CircuitBreaker(name = "auth-service", fallbackMethod = "useUserPointFallback")
    public void useUserPoint(Long userId, int point, String description) {
        log.info("사용자 포인트 사용 요청");
        authServiceClient.useUserPoint(userId, point, description); // FeignClient 호출
    }

    // fallback 메서드
    public void useUserPointFallback(Long userId, int point, String description, Throwable t) {
        Throwable cause = t.getCause();

        if (cause == null) {
            log.error("(useUserPointFallback) 작동! 원인: {}", "에러 정보가 없습니다.");
        } else {
            log.error("(useUserPointFallback) 작동! 원인: {}", cause.getMessage());
        }

        // 서버가 죽었거나 연결이 안 됨 (보통 503)
        if (cause instanceof FeignException.BadRequest){
            // 사용자가 없으면 에러
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        } else if (cause instanceof FeignException.Conflict){
            // 포인트 부족 시 에러
            throw new GeneralException(CorrectionErrorStatus._NOT_ENOUGH_POINT);
        } else if (cause instanceof FeignException.InternalServerError){
            // 서버에러
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }

        // 서버가 죽었거나 연결이 안 됨 (보통 503)
        throw new GeneralException(ErrorStatus._AUTH_SERVICE_UNAVAILABLE);
    }

    @CircuitBreaker(name = "auth-service", fallbackMethod = "enoughUserPointFallback")
    public boolean enoughUserPoint(Long userId, int point) {
        log.info("사용자 포인트가 충분한지 검증 요청");
        return authServiceClient.enoughUserPoint(userId, point); // FeignClient 호출
    }

    // fallback 메서드
    public boolean enoughUserPointFallback(Long userId, int point, Throwable t) {
        Throwable cause = t.getCause();

        if (cause == null) {
            log.error("(enoughUserPointFallback) 작동! 원인: {}", "에러 정보가 없습니다.");
        } else {
            log.error("(enoughUserPointFallback) 작동! 원인: {}", cause.getMessage());
        }

        if (cause instanceof FeignException.BadRequest){
            // 사용자가 없으면 에러
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        } else if (cause instanceof FeignException.InternalServerError){
            // 서버에러
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }

        // 서버가 죽었거나 연결이 안 됨 (보통 503)
        throw new GeneralException(ErrorStatus._AUTH_SERVICE_UNAVAILABLE);
    }
}
