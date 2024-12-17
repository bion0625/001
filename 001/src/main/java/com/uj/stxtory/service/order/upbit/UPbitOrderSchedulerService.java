package com.uj.stxtory.service.order.upbit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.uj.stxtory.service.account.upbit.UPbitAccountService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UPbitOrderSchedulerService {
	
	private UPbitAccountService accountService;
	
	public UPbitOrderSchedulerService(UPbitAccountService accountService) {
		this.accountService = accountService;
	}

//	@Scheduled(fixedDelay = 60000)
	public void upbitAutoOrder() {
		log.info("auto account: " + accountService.getAutoAccount());
	}
}
