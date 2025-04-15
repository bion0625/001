package com.uj.stxtory.repository;

import com.uj.stxtory.domain.entity.StockHistoryLabel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockHistoryLabelRepository extends JpaRepository<StockHistoryLabel, Long> {
    Optional<StockHistoryLabel> findByCodeAndName(String code, String name);
}
