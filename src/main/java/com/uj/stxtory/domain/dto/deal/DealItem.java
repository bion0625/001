package com.uj.stxtory.domain.dto.deal;

import java.util.Date;

public interface DealItem {

    // 하한 및 기대 매도 가격 업데이트
    void sellingPriceUpdate(Date pricingDate);

    String getCode();
    String getName();
    long getExpectedSellingPrice();
    long getMinimumSellingPrice();
}
