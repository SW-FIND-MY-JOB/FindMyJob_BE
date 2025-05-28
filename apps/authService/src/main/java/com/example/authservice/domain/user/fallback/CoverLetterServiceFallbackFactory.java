package com.example.authservice.domain.user.fallback;

import com.example.authservice.domain.user.client.CoverLetterServiceClient;
import com.example.authservice.domain.user.client.JobServiceClient;
import com.example.authservice.global.exception.GeneralException;
import com.example.responselib.apiPayload.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CoverLetterServiceFallbackFactory implements FallbackFactory<CoverLetterServiceClient> {

    @Override
    public CoverLetterServiceClient create(Throwable cause) {
        return new CoverLetterServiceClient() {
            @Override
            public void deleteUserCoverLetterScraps(Long userId) {
                log.error("사용자에 따른 자소서 스크랩 정보 삭제 에러: {}", cause.getMessage());
                throw new GeneralException(ErrorStatus._COVER_LETTER_SERVICE_UNAVAILABLE);
            }
        };
    }
}
