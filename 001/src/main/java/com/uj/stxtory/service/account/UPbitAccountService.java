package com.uj.stxtory.service.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.uj.stxtory.domain.dto.key.UPbitKey;
import com.uj.stxtory.domain.dto.upbit.UPbitAccount;
import com.uj.stxtory.domain.entity.TbUPbitKey;
import com.uj.stxtory.repository.TbUPbitKeyRepository;

@Service
@Transactional(readOnly = true)
public class UPbitAccountService {

	private TbUPbitKeyRepository keyRepository;
	private RestTemplate restTemplate;

	public UPbitAccountService(TbUPbitKeyRepository keyRepository, RestTemplate restTemplate) {
		this.keyRepository = keyRepository;
		this.restTemplate = restTemplate;
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
	
	@Transactional
	public void updateAuto(String loginId, boolean auto) {
		keyRepository.findByUserLoginId(loginId).map(e -> {
			e.setAutoOn(auto);
			return e;
		}).orElseThrow();
	}
	
	public boolean isAuto(String loginId) {
		return keyRepository.findByUserLoginId(loginId).map(e -> e.getAutoOn()).orElse(false);
	}

	private Optional<UPbitKey> getKeyByLoginId(String loginId) {
		return keyRepository.findByUserLoginId(loginId).map(e -> UPbitKey.fromEntity(e));
	}

	private String getAuthenticationToken(String loginId) {
		return getKeyByLoginId(loginId).map(account -> {
			// JWT 생성
			Algorithm algorithm = Algorithm.HMAC256(account.getSecret());
			String jwtToken = JWT.create().withClaim("access_key", account.getAccess())
					.withClaim("nonce", UUID.randomUUID().toString()).sign(algorithm);

			return "Bearer " + jwtToken;
		}).orElse("");
	}

	/**
	 * 전체 계좌 조회 GUIDE:
	 * docs.upbit.com/reference/%EC%A0%84%EC%B2%B4-%EA%B3%84%EC%A2%8C-%EC%A1%B0%ED%9A%8C
	 */
	public List<UPbitAccount> getAccount(String loginId) {
		List<UPbitAccount> accounts = new ArrayList<>();

		String serverUrl = "https://api.upbit.com/v1/accounts";

		String authenticationToken = getAuthenticationToken(loginId);
		
		if (authenticationToken.isEmpty()) return accounts;

		// 요청 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("Authorization", authenticationToken);

		// 요청 생성
		try {
			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<List<UPbitAccount>> response = restTemplate.exchange(serverUrl, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<UPbitAccount>>() {
					});
			accounts = response.getBody();
		} catch (Exception e) {
			accounts = new ArrayList<>();
		}
		return accounts;
	}
}
