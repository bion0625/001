package com.uj.stxtory.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uj.stxtory.domain.dto.key.UPbitKey;
import com.uj.stxtory.service.account.UPbitAccountService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UPbitAccountController {
	
	private final UPbitAccountService accountService;
	
	public UPbitAccountController(UPbitAccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping("/upbit/key")
	public String getUpbitKey() {
		return "upbit/key";
	}
	
	@PostMapping("/upbit/key")
	public String insertUpbitKey(UPbitKey key, Authentication authentication) {
		accountService.insertKey(key, authentication.getPrincipal().toString());
		return "upbit/key";
	}
	
	@PutMapping("/upbit/auto")
	@ResponseBody
	public void updateAuto(@RequestBody Map<String, Boolean> payload, Authentication authentication) {
		accountService.updateAuto(authentication.getPrincipal().toString(), payload.get("auto"));
	}
}
