package com.uj.stxtory.service.account;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uj.stxtory.domain.dto.key.UPbitKey;
import com.uj.stxtory.domain.entity.TbUPbitKey;
import com.uj.stxtory.repository.TbUPbitKeyRepository;

@Service
@Transactional(readOnly = true)
public class UPbitAccountService {
	
	private TbUPbitKeyRepository keyRepository;
	
	public UPbitAccountService(TbUPbitKeyRepository keyRepository) {
		this.keyRepository = keyRepository;
	}
	
	public Optional<UPbitKey> getKeyByLoginId(String loginId) {
		return keyRepository.findByUserLoginId(loginId).map(e -> UPbitKey.fromEntity(e));
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
