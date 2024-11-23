package com.uj.stxtory.repository;

import com.uj.stxtory.domain.entity.UPbit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UPbitRepository extends JpaRepository<UPbit, Long> {
    List<UPbit> findAllByDeletedAtIsNull();
}
