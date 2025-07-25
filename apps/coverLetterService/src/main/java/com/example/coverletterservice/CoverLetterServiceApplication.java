package com.example.coverletterservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableScheduling
@EnableFeignClients(basePackages = "com.example.coverletterservice.domain.coverLetter.client")
public class CoverLetterServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoverLetterServiceApplication.class, args);
	}

}
