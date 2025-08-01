package com.uj.stxtory.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DividendStock extends Base {
  @Id @GeneratedValue private Long id;

  @Column(name = "code", nullable = false)
  private String code;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "dividend_rate", nullable = false)
  private Double dividendRate;

  public static DividendStock of(String code, String name, Double dividendRate) {
    if (code == null || name == null || dividendRate == null) return null;
    return DividendStock.builder().code(code).name(name).dividendRate(dividendRate).build();
  }
}
