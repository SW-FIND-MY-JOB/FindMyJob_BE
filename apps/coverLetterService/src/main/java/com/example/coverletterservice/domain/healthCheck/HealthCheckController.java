package com.example.coverletterservice.domain.healthCheck;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @Value("${config.health-check}")
    private String healthCheck = "cover-letter-service health bad...";

    @GetMapping("/check")
    public Map<String, String> healthCheck(){
        return Map.of(
                "health-check", healthCheck
        );
    }
}
