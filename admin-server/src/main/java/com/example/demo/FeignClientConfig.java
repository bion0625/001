package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {
    @Value("${admin-login-service.url}")
    private String adminLoginServiceUrl;

    @Bean
    public String adminLoginServiceUrl() {
        return adminLoginServiceUrl;
    }
}
