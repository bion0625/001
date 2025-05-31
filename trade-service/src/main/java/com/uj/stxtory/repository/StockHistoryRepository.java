package com.uj.stxtory.repository;

import com.uj.stxtory.domain.entity.StockHistory;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
  Optional<StockHistory> findByCodeAndNameAndCreatedAt(
      String code, String name, LocalDateTime createdAt);
}
