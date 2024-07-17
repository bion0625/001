package com.uj.stxtory.repository;

import com.uj.stxtory.domain.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findAllByDeletedAtIsNull();

    Optional<Stock> findByCodeAndDeletedAtIsNull(String code);
}
