package com.uj.stxtory.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.uj.stxtory.domain.dto.deal.DealItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Stock extends Base implements DealItem {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    /** 타깃 최초 하한 매도 가격 */
    @Column(name = "origin_minimum_selling_price", nullable = false)
    private long originMinimumSellingPrice;

    /** 타깃 최초 기대 매도 가격 */
    @Column(name = "origin_expected_selling_price", nullable = false)
    private long originExpectedSellingPrice;

    /** 타깃 하한 매도 가격 */
    @Column(name = "minimum_selling_price", nullable = false)
    private long minimumSellingPrice;

    /** 타깃 기대 매도 가격 */
    @Column(name = "expected_selling_price", nullable = false)
    private long expectedSellingPrice;

    /** 가격 설정 기준 날짜 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "pricing_reference_date", nullable = true)
    private LocalDateTime pricingReferenceDate = LocalDateTime.now();

    /** 갱신 횟수 */
    @Column(name = "renewal_cnt", nullable = false)
    private int renewalCnt;

    public Stock(
            String code,
            String name,
            long originMinimumSellingPrice,
            long originExpectedSellingPrice,
            long minimumSellingPrice,
            long expectedSellingPrice
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

    // 하한 및 기대 매도 가격 업데이트
    public void sellingPriceUpdate(Date pricingDate) {
        // 하한 매도 가격은 반올림
        this.minimumSellingPrice = Math.round(this.expectedSellingPrice * 0.95);
        // 기대 매도 가격은 올림
        this.expectedSellingPrice = (long) Math.ceil(this.expectedSellingPrice * 1.1);

        this.renewalCnt++;

        this.pricingReferenceDate = pricingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        this.setUpdatedAt(LocalDateTime.now());
    }
}
