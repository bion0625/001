package com.uj.stxtory.repository;

import com.uj.stxtory.domain.entity.UPbit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "sql_upbit")
public interface UPbitRepository extends JpaRepository<UPbit, Long> {
    List<UPbit> findAllByDeletedAtIsNull();
}
