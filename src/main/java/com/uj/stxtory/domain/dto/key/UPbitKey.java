package com.uj.stxtory.domain.dto.key;

import com.uj.stxtory.domain.entity.TbUPbitKey;

import lombok.Data;

@Data
public class UPbitKey {
	private String access;
	private String secret;
	
	public TbUPbitKey toEntity(String userLoginId) {
		TbUPbitKey entity = new TbUPbitKey();
		entity.setAccessKey(this.access);
		entity.setSecretKey(this.secret);
		entity.setUserLoginId(userLoginId);
		return entity;
	}
}
