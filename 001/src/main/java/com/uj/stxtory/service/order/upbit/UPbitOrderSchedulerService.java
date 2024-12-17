package com.uj.stxtory.service.order.upbit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uj.stxtory.domain.dto.upbit.UPbitInfo;
import com.uj.stxtory.domain.dto.upbit.UpbitOrderChanceResponse;
import com.uj.stxtory.service.deal.notify.UPbitNotifyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.uj.stxtory.domain.dto.upbit.UPbitAccount;
import com.uj.stxtory.domain.entity.TbUPbitKey;
import com.uj.stxtory.service.account.upbit.UPbitAccountService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UPbitOrderSchedulerService {

	private final UPbitAccountService accountService;
	private final UPbitNotifyService uPbitNotifyService;

	public UPbitOrderSchedulerService(UPbitAccountService accountService, UPbitNotifyService uPbitNotifyService) {
		this.accountService = accountService;
		this.uPbitNotifyService = uPbitNotifyService;
	}

	// 매매 스케쥴러
	@Scheduled(fixedDelay = 60000)
	public void upbitAutoOrder() {

		// 타입값 확인
		Map<Object, List<TbUPbitKey>> accountGroup = accountService.getAutoAccount().stream().collect(Collectors.groupingBy(key -> {
			Optional<List<UPbitAccount>> account = Optional.ofNullable(accountService.getAccount(key.getUserLoginId()));
			if (account.filter(a -> a.size() > 1).isPresent()) return "IN";
			if (account.filter(a -> a.size() == 1).isPresent()) return "OUT";
			return "NOT ACCOUNT";
		}));

		Optional.ofNullable(accountGroup.get("NOT ACCOUNT")).orElse(new ArrayList<>())
				.forEach(a -> log.info("{}is not account!", a));

		// 이미 가지고 있으니, 매도할지 판단 후 실행
		Optional.ofNullable(accountGroup.get("IN")).map(in -> {
			in.forEach(this::checkAndSale);
			return true;
		});

		// 가지고 있는 게 없으니, 매수할지 판단 후 실행
		Optional.ofNullable(accountGroup.get("OUT")).map(out -> {
			out.forEach(this::checkAndBuy);
			return true;
		});

	}

	// 매수부터 확인
	private void checkAndBuy(TbUPbitKey key) {
		List<String> markets = uPbitNotifyService.getSaved().stream().map(UPbitInfo::getCode).collect(Collectors.toList());
		if (markets.size() < 3) return;
		markets.forEach(m -> {
			List<UPbitAccount> account = accountService.getAccount(key.getUserLoginId());
			if (account == null || account.isEmpty()) return;
			// 현재 가진 돈
			double balance = Double.parseDouble(account.get(0).getBalance());
			UpbitOrderChanceResponse ordersChance = accountService.getOrdersChance(key.getAccessKey(), key.getSecretKey(), m);
			// 매수 수수료 비율
			double bidFee = Double.parseDouble(ordersChance.getBidFee());
			// 매수 요청 가격
			double dPrice = (1d - bidFee) * balance;
			String price = String.valueOf(dPrice);

			// 최소 주문 금액보다 크다면 주문
			if (dPrice > Double.parseDouble(ordersChance.getMarket().getBid().getMinTotal()))
				accountService.order(m, price, "bid", key.getAccessKey(), key.getSecretKey());
		});
	}

	private void checkAndSale(TbUPbitKey key) {
		List<String> markets = uPbitNotifyService.getSaved().stream().map(UPbitInfo::getCode).collect(Collectors.toList());
		List<UPbitAccount> originalAccount = accountService.getAccount(key.getUserLoginId());
		List<UPbitAccount> account = originalAccount.stream().filter(a -> !a.getCurrency().contains("KRW")).collect(Collectors.toList());
		account.forEach(a -> {
			UpbitOrderChanceResponse ordersChance = accountService.getOrdersChance(key.getAccessKey(), key.getSecretKey(), "KRW-" + a.getCurrency());
			String balance = ordersChance.getAskAccount().getBalance();
			// 현재 추천 종목에 포함되지 않거나 추천 종목이 3개 미만이면 곧장 매도
			if (!markets.contains("KRW-" + a.getCurrency()) || markets.size() < 3) accountService.order("KRW-" + a.getCurrency(), balance, "ask", key.getAccessKey(), key.getSecretKey());
		});
	}
}
