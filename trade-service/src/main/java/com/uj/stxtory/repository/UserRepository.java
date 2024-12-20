package com.uj.stxtory.repository;

import com.uj.stxtory.domain.entity.TbUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<TbUser, Long> {
    Optional<TbUser> findByUserLoginIdAndDeletedAtIsNull(String userLoginId);
}
