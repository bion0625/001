package com.example.demo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "prd", url = "#{@adminLoginServiceUrl}", configuration = FeignClientConfig.class)
public interface AdminLoginClient {
    @PostMapping("/actuator/adminLogin")
    String adminLogin(@RequestBody String credentials);
}
