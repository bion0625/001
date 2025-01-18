package com.uj.stxtory.repository;

import com.uj.stxtory.domain.entity.USStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "sql_usstock")
public interface USStockRepository extends JpaRepository<USStock, Long> {
    List<USStock> findAllByDeletedAtIsNullOrderByPricingReferenceDateDesc();
}
