package com.example.coverletterservice.domain.coverLetter.fallbackService;

import com.example.coverletterservice.domain.coverLetter.client.AuthServiceClient;
import com.example.coverletterservice.global.exception.GeneralException;
import com.example.responselib.apiPayload.status.ErrorStatus;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthFallbackService {
    private  final AuthServiceClient authServiceClient;

    @CircuitBreaker(name = "auth-service", fallbackMethod = "addUserPointFallback")
    public void addUserPoint(Long userId, int point, String description){
        log.info("[사용자 포인트 적립 요청]");
        authServiceClient.addUserPoint(userId, point, description);
    }

    public void addUserPointFallback(Long userId, int point, String description, Throwable t){
        Throwable cause = t.getCause();

        if (cause == null) {
            log.error("(addUserPointFallback) 작동! 원인: {}", "에러 정보가 없습니다.");
        } else {
            log.error("(addUserPointFallback) 작동! 원인: {}", cause.getMessage());
        }

        if (cause instanceof FeignException.BadRequest) {
            //존재하지 않는 사용자임
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        } else if (cause instanceof FeignException.InternalServerError){
            //서버 에러
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }

        // 서버가 죽었거나 연결이 안 됨 (보통 503)
        throw new GeneralException(ErrorStatus._AUTH_SERVICE_UNAVAILABLE);
    }
}
