package com.example.authservice.domain.user.client;

import com.example.authservice.domain.user.fallback.CoverLetterServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(
        name = "cover-letter-service",
        path = "/internal/cover-letter-scraps",
        fallbackFactory = CoverLetterServiceFallbackFactory.class
)
public interface CoverLetterServiceClient {

    @DeleteMapping("/{userId}")
    void deleteUserCoverLetterScraps(@PathVariable("userId") Long userId);
}
