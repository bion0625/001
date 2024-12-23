package com.uj.stxtory.domain.dto.deal;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface DealItem {

    // 하한 및 기대 매도 가격 업데이트
    void sellingPriceUpdate(Date pricingDate);

    String getCode();
    String getName();
    double getExpectedSellingPrice();
    double getMinimumSellingPrice();
    void setTempPrice(double price);
    double getTempPrice();
    int getRenewalCnt();

    Object toEntity();
    void setPrices(List<DealPrice> prices);
    LocalDateTime getPricingReferenceDate();
}
