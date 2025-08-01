package com.uj.stxtory.repository;

import com.uj.stxtory.domain.entity.DividendStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DividendStockRepository extends JpaRepository<DividendStock, Long> {
  Optional<DividendStock> findByCodeAndDeletedAtIsNull(String Code);
  List<DividendStock> findAllByDeletedAtIsNull();
}
