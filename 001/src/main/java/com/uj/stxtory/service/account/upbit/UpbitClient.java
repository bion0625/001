package com.uj.stxtory.service.account.upbit;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.uj.stxtory.domain.dto.upbit.UPbitAccount;

@FeignClient(name = "upbitClient", url = "https://api.upbit.com")
public interface UpbitClient {
	@GetMapping("/v1/accounts")
    List<UPbitAccount> getAccount(@RequestHeader("Authorization") String authorizationToken);
	
	@PostMapping(value = "/v1/orders", consumes = MediaType.APPLICATION_JSON_VALUE)
    String placeOrder(
            @RequestHeader("Authorization") String authorizationToken,
            @RequestBody Map<String, String> orderRequest
    );
	
	@GetMapping("/v1/orders/chance")
    String getOrdersChance(
            @RequestHeader("Authorization") String authorizationToken,
            @RequestParam("market") String market
    );
}
