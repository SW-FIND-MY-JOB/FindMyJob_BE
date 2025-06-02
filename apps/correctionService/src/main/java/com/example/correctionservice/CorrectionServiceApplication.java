package com.example.correctionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.example.correctionservice.domain.correction.client")
public class CorrectionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CorrectionServiceApplication.class, args);
	}

}
