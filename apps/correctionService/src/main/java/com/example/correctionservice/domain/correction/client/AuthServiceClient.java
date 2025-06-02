package com.example.correctionservice.domain.correction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(
        name = "auth-service",
        path = "/internal/users"
)
public interface AuthServiceClient {
    //포인트 사용
    @PutMapping("/{userId}/use-point")
    void useUserPoint(@PathVariable Long userId,
                        @RequestParam(defaultValue = "300") int point);
}
