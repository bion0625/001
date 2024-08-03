package com.uj.stxtory.repository;

import com.uj.stxtory.domain.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findAllByDeletedAtIsNull();

    List<Stock> findAllByDeletedAtAfter(LocalDateTime DeletedAt);
}
