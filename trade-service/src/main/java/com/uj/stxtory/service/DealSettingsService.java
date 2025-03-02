package com.uj.stxtory.service;

import com.uj.stxtory.domain.dto.deal.DealSettingsInfo;
import com.uj.stxtory.repository.DealSettingsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
public class DealSettingsService {

	private final DealSettingsRepository dealSettingsRepository;

	public DealSettingsService(DealSettingsRepository dealSettingsRepository) {
		this.dealSettingsRepository = dealSettingsRepository;
	}

	public DealSettingsInfo getByName(String name) {
		return dealSettingsRepository.findByNameAndDeletedAtIsNull(name)
				.map(DealSettingsInfo::fromEntity)
				.orElseGet(() -> DealSettingsInfo.basic(name));
	}

	@Transactional
	public void update(DealSettingsInfo info) {
		dealSettingsRepository.findByNameAndDeletedAtIsNull(info.getName())
				.map(entity -> {
					entity.setExpectedHighPercentage(info.getExpectedHighPercentage());
					entity.setExpectedLowPercentage(info.getExpectedLowPercentage());
					entity.setHighestPriceReferenceDays(info.getHighestPriceReferenceDays());
					entity.setUpdatedAt(LocalDateTime.now());
					return entity;
				})
				.orElseGet(() -> dealSettingsRepository.save(info.toEntity()));
	}
}
