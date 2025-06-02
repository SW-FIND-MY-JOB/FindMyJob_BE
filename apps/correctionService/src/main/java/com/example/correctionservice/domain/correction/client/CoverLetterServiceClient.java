package com.example.correctionservice.domain.correction.client;

import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(
        name = "cover-letter-service",
        path = "/internal/cover-letter"
)
public interface CoverLetterServiceClient {
    //자소서 내용 가져오기
    @GetMapping
    ResponseEntity<String> getCoverLetterContent(@RequestParam @NotNull Long coverLetterId);
}
