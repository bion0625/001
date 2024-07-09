package com.uj.stxtory.repository;

import com.uj.stxtory.domain.entity.TargetMail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TargetEailRepository extends JpaRepository<TargetMail, String> {
}
