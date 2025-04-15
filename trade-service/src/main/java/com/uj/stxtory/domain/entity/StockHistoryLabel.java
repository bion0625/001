package com.uj.stxtory.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
public class StockHistoryLabel extends Base {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "code", nullable = false)
    private final String code;

    @Column(name = "name", nullable = false)
    private final String name;
}
