package com.example.correctionservice.domain.correction.client;

import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
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
    void useUserPoint(@PathVariable @NotNull Long userId,
                        @RequestParam @NotNull int point);

    //포인트 사용 가능한지 조회
    @GetMapping("/{userId}/enough-point")
    boolean enoughUserPoint(@PathVariable @NotNull Long userId,
                            @RequestParam @NotNull int point);
}
