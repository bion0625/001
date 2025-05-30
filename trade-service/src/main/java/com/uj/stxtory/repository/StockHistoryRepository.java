package com.uj.stxtory.repository;

import com.uj.stxtory.domain.entity.StockHistory;
import com.uj.stxtory.domain.entity.StockHistoryLabel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
  Optional<StockHistoryLabel> findByCodeAndName(String code, String name);
}
