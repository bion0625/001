package com.uj.stxtory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.uj.stxtory.domain.entity.TbUPbitKey;

@RepositoryRestResource(path = "upbit_key")
public interface TbUPbitKeyRepository extends JpaRepository<TbUPbitKey, Long> {
	Optional<TbUPbitKey> findByUserLoginId(String userLoginId);
}
