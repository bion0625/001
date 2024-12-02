package com.uj.stxtory.service.account;

import org.springframework.stereotype.Service;

import com.uj.stxtory.domain.dto.key.UPbitKey;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UPbitAccountService {

	public void insertKey(UPbitKey key) {
		log.info("access: " + key.getAccess());
		log.info("secret: " + key.getSecret());
	}
}
