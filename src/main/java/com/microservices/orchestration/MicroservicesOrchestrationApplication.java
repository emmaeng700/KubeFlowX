package com.microservices.orchestration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroservicesOrchestrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicroservicesOrchestrationApplication.class, args);
    }
} 