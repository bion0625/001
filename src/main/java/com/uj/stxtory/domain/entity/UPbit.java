package com.uj.stxtory.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UPbit extends Base {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    /** 타깃 최초 하한 매도 가격 */
    @Column(name = "origin_minimum_selling_price", nullable = false, precision = 10, scale = 6)
    private double originMinimumSellingPrice;

    /** 타깃 최초 기대 매도 가격 */
    @Column(name = "origin_expected_selling_price", nullable = false, precision = 10, scale = 6)
    private double originExpectedSellingPrice;

    /** 타깃 하한 매도 가격 */
    @Column(name = "minimum_selling_price", nullable = false, precision = 10, scale = 6)
    private double minimumSellingPrice;

    /** 타깃 기대 매도 가격 */
    @Column(name = "expected_selling_price", nullable = false, precision = 10, scale = 6)
    private double expectedSellingPrice;

    /** 가격 설정 기준 날짜 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "pricing_reference_date", nullable = true)
    private LocalDateTime pricingReferenceDate = LocalDateTime.now();

    /** 갱신 횟수 */
    @Column(name = "renewal_cnt", nullable = false)
    private int renewalCnt;

    public UPbit(
            String code,
            String name,
            double originMinimumSellingPrice,
            double originExpectedSellingPrice,
            double minimumSellingPrice,
            double expectedSellingPrice
    ) {
        this.code = code;
        this.name = name;
        this.originMinimumSellingPrice = originMinimumSellingPrice;
        this.originExpectedSellingPrice = originExpectedSellingPrice;
        this.minimumSellingPrice = minimumSellingPrice;
        this.expectedSellingPrice = expectedSellingPrice;
        this.renewalCnt = 0;
        // 날짜만 남기고 나머지 오후 5시로 설정
        this.setCreatedAt(LocalDateTime.now());
    }
}