package com.uj.stxtory.domain.dto.stock;

import com.uj.stxtory.domain.entity.Stock;
import lombok.Data;

import java.util.List;

@Data
public class StockInfo {
    private String name; // 종목명
    private String code; // 종목코드
    private int totalPage; // 네이버에서 가져올 전체 페이지
    private List<StockPriceInfo> prices; // 가격정보 리스트

    public Stock toEntity() {
        long high = prices.get(0).getHigh();
        long minimum = Math.round(high * 0.95);
        long expected = Math.round(high * 1.1);
        return new Stock(code, name, minimum, expected, minimum, expected);
    }
}
