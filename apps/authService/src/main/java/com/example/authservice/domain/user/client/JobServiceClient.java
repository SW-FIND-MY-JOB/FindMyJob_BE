package com.example.authservice.domain.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(
        name = "job-service",
        path = "/internal/notice-scraps"
)
public interface JobServiceClient {

    @DeleteMapping("/{userId}")
    void deleteUserNoticeScraps(@PathVariable("userId") Long userId);
}
