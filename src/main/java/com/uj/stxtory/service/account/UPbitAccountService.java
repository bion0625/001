package com.uj.stxtory.service.account;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.uj.stxtory.domain.dto.key.UPbitKey;
import com.uj.stxtory.domain.entity.TbUPbitKey;
import com.uj.stxtory.repository.TbUPbitKeyRepository;

@Service
public class UPbitAccountService {
	
	private TbUPbitKeyRepository keyRepository;
	
	public UPbitAccountService(TbUPbitKeyRepository keyRepository) {
		this.keyRepository = keyRepository;
	}

	@Transactional
	public void insertKey(UPbitKey key, String loginId) {
		Optional<TbUPbitKey> entity = keyRepository.findByUserLoginId(loginId).map(e -> {
			e.setAccessKey(key.getAccess());
			e.setSecretKey(key.getSecret());
			return e;
		});
		if (entity.isEmpty()) {
			keyRepository.save(key.toEntity(loginId));
		}
	}
}
