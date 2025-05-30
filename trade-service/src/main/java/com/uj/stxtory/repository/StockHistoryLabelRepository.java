package com.uj.stxtory.repository;

import com.uj.stxtory.domain.entity.StockHistoryLabel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockHistoryLabelRepository extends JpaRepository<StockHistoryLabel, Long> {
  Optional<StockHistoryLabel> findByCodeAndName(String code, String name);
}
