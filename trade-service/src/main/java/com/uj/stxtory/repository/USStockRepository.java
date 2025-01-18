package com.uj.stxtory.repository;

import com.uj.stxtory.domain.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "sql_stock")
public interface USStockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findAllByDeletedAtIsNullOrderByPricingReferenceDateDesc();
}
