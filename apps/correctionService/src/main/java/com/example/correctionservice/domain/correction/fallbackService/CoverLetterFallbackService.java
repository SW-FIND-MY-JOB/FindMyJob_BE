package com.example.correctionservice.domain.correction.fallbackService;

import com.example.correctionservice.domain.correction.client.CoverLetterServiceClient;
import com.example.correctionservice.domain.correction.exception.status.CorrectionErrorStatus;
import com.example.correctionservice.global.exception.GeneralException;
import com.example.responselib.apiPayload.status.ErrorStatus;
import feign.FeignException;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoverLetterFallbackService {
    private final CoverLetterServiceClient coverLetterServiceClient;

    @CircuitBreaker(name = "cover-letter-service", fallbackMethod = "getCoverLetterContentPointFallback")
    public String getCoverLetterContent(Long coverLetterId) {
        log.info("자소서 요청: {}", coverLetterId);
        return coverLetterServiceClient.getCoverLetterContent(coverLetterId).getBody();
    }

    public String getCoverLetterContentPointFallback(Long coverLetterId, Throwable t){
        Throwable cause = t.getCause();

        if (cause == null) {
            log.error("(getCoverLetterContentPointFallback) 작동! 원인: {}", "에러 정보가 없습니다.");

        } else{
            log.error("(getCoverLetterContentPointFallback) 작동! 원인: {}", cause.getMessage());
        }

        // 서버가 죽었거나 연결이 안 됨 (보통 503)
        if (cause instanceof FeignException.ServiceUnavailable || cause instanceof RetryableException){
            throw new GeneralException(ErrorStatus._COVER_LETTER_SERVICE_UNAVAILABLE);
        } else if(cause instanceof FeignException.BadRequest){
            //자소서가 없다면
            throw new GeneralException(CorrectionErrorStatus._NOT_EXIST_COVER_LETTER);
        }

        // 예외가 무엇인지 명확하지 않은 경우
        throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
    }
}
