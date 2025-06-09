package com.example.authservice.domain.user.fallbackService;

import com.example.authservice.domain.user.client.CoverLetterServiceClient;
import com.example.authservice.global.exception.GeneralException;
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
public class CoverLetterFallbackService {
    private final CoverLetterServiceClient coverLetterServiceClient;

    @CircuitBreaker(name = "cover-letter-service", fallbackMethod = "deleteUserCoverLetterScrapsFallback")
    public void deleteUserCoverLetterScraps(@PathVariable("userId") Long userId){
        log.info("자소서 공고 스크랩 삭제 요청");
        coverLetterServiceClient.deleteUserCoverLetterScraps(userId);
    }

    public void deleteUserCoverLetterScrapsFallback(@PathVariable("userId") Long userId, Throwable t) {
        Throwable cause = t.getCause();

        if (cause == null) {
            log.error("(deleteUserCoverLetterScrapsFallback) 작동! 원인: {}", "에러 정보가 없습니다.");
        }else{
            log.error("(deleteUserCoverLetterScrapsFallback) 작동! 원인: {}", cause.getMessage());
        }

        //서버에러
        if (cause instanceof FeignException.InternalServerError){
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }

        // 서버가 죽었거나 연결이 안 됨 (보통 503)
        throw new GeneralException(ErrorStatus._COVER_LETTER_SERVICE_UNAVAILABLE);
        
    }
}
