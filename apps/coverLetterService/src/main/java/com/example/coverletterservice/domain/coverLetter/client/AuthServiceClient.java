package com.example.coverletterservice.domain.coverLetter.client;
import com.example.coverletterservice.domain.coverLetter.fallback.AuthServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(
        name = "auth-service",
        path = "/internal/users",
        fallbackFactory = AuthServiceFallbackFactory.class
)
public interface AuthServiceClient {
    //포인트 적립
    @PutMapping("/{userId}/add-point")
    void addUserPoint (@PathVariable Long userId, @RequestParam(defaultValue = "500") int point);
}
