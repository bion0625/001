package com.uj.stxtory.repository;

import com.uj.stxtory.domain.entity.DealSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "sql_deal_settings")
public interface DealSettingsRepository extends JpaRepository<DealSettings, Long> {
    Optional<DealSettings> findByNameAndDeletedAtIsNull(String name);
}
