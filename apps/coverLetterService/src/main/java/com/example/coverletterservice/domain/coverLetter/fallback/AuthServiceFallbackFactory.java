package com.example.coverletterservice.domain.coverLetter.fallback;

import com.example.coverletterservice.domain.coverLetter.client.AuthServiceClient;
import com.example.coverletterservice.global.exception.GeneralException;
import com.example.responselib.apiPayload.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthServiceFallbackFactory implements FallbackFactory<AuthServiceClient> {
    @Override
    public AuthServiceClient create(Throwable cause) {
        return new AuthServiceClient() {
            @Override
            public void addUserPoint(Long userId, int point) {
                log.error("사용자 포인트 적립 에러: {}", cause.getMessage());
                throw new GeneralException(ErrorStatus._AUTH_SERVICE_UNAVAILABLE);
            }
        };
    }
}
