package com.uj.stxtory.service.order;

import java.util.List;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.uj.stxtory.domain.dto.upbit.UPbitAccount;

// TODO 추후에 openFeign으로 변경
// 일단 restTemplate을 빈으로 등록 예정
public class UPbitOrderService {
	public static void main(String[] args) {
		String accessKey = ""; // DB에서 가져오기
		String secretKey = "";

		UPbitOrderService order = new UPbitOrderService();
		order.getNow(accessKey, secretKey);
	}

	/**
	 * 전체 계좌 조회 GUIDE:
	 * docs.upbit.com/reference/%EC%A0%84%EC%B2%B4-%EA%B3%84%EC%A2%8C-%EC%A1%B0%ED%9A%8C
	 */
	public void getNow(String accessKey, String secretKey) {
		String serverUrl = "https://api.upbit.com/v1/accounts";

		// JWT 생성
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		String jwtToken = JWT.create().withClaim("access_key", accessKey)
				.withClaim("nonce", UUID.randomUUID().toString()).sign(algorithm);

		String authenticationToken = "Bearer " + jwtToken;

		// RestTemplate 사용
		RestTemplate restTemplate = new RestTemplate();

		// 요청 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("Authorization", authenticationToken);

		// 요청 생성
		HttpEntity<String> entity = new HttpEntity<>(headers);
		
		ResponseEntity<List<UPbitAccount>> response = restTemplate
				.exchange(serverUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<List<UPbitAccount>>() {});
		
		List<UPbitAccount> accounts = response.getBody();
		// 응답 출력
		accounts.forEach(account -> {
            System.out.println("Currency: " + account.getCurrency());
            System.out.println("Balance: " + account.getBalance());
            System.out.println("Locked: " + account.getLocked());
            // 기타 필요한 정보 출력
        });
	}

}
