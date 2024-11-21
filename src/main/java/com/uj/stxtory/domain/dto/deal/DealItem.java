package com.uj.stxtory.domain.dto.deal;

import com.uj.stxtory.domain.entity.Stock;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@AllArgsConstructor
@Getter
public class DealItem {
    private String code;
    private String name;
    private long minimumSellingPrice;
    private long expectedSellingPrice;
    private LocalDateTime pricingReferenceDate = LocalDateTime.now();
    private int renewalCnt;

    public static DealItem fromStock(Stock stock) {
        return new DealItem(
                stock.getCode(),
                stock.getName(),
                stock.getMinimumSellingPrice(),
                stock.getExpectedSellingPrice(),
                stock.getPricingReferenceDate(),
                stock.getRenewalCnt());
    }

    // 하한 및 기대 매도 가격 업데이트
    public void sellingPriceUpdate(Date pricingDate) {
        // 하한 매도 가격은 반올림
        this.minimumSellingPrice = Math.round(this.expectedSellingPrice * 0.95);
        // 기대 매도 가격은 올림
        this.expectedSellingPrice = (long) Math.ceil(this.expectedSellingPrice * 1.1);

        this.renewalCnt++;

        this.pricingReferenceDate = pricingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    public String toString() {
        return "DealItem{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", minimumSellingPrice=" + minimumSellingPrice +
                ", expectedSellingPrice=" + expectedSellingPrice +
                ", pricingReferenceDate=" + pricingReferenceDate +
                ", renewalCnt=" + renewalCnt +
                '}';
    }
}
