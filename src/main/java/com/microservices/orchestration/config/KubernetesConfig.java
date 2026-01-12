package com.microservices.orchestration.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@org.springframework.context.annotation.Configuration
public class KubernetesConfig {

    @Bean
    @Primary
    public ApiClient apiClient() {
        try {
            ApiClient client = Config.defaultClient();
            Configuration.setDefaultApiClient(client);
            return client;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Kubernetes client", e);
        }
    }
} 