package com.uj.stxtory.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.uj.stxtory.domain.dto.key.UPbitKey;
import com.uj.stxtory.service.account.UPbitAccountService;

@Controller
public class UPbitAccountController {
	
	private final UPbitAccountService accountService;
	
	public UPbitAccountController(UPbitAccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping("/upbit/key")
	public String getUpbitKey() {
		return "/upbit/key.html";
	}
	
	@PostMapping("/upbit/key")
	public String insertUpbitKey(UPbitKey key, Authentication authentication) {
		accountService.insertKey(key, authentication.getPrincipal().toString());
		return "/upbit/key.html";
	}
}
