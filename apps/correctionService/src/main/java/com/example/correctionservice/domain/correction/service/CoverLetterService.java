package com.example.correctionservice.domain.correction.service;

import com.example.correctionservice.domain.correction.client.CoverLetterServiceClient;
import com.example.correctionservice.global.exception.GeneralException;
import com.example.responselib.apiPayload.status.ErrorStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoverLetterService {
    private final CoverLetterServiceClient coverLetterServiceClient;

    @CircuitBreaker(name = "coverLetterService", fallbackMethod = "getCoverLetterContentPointFallback")
    public String getCoverLetterContent(Long coverLetterId) {
        return coverLetterServiceClient.getCoverLetterContent(coverLetterId);
    }

    public void getCoverLetterContentPointFallback(Long coverLetterId, Throwable t){
        log.error("Circuit breaker fallback 작동! 원인: {}", t.getMessage());
        throw new GeneralException(ErrorStatus._COVER_LETTER_SERVICE_UNAVAILABLE);
    }
}
