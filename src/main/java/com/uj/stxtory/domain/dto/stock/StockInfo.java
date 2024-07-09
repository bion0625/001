package com.uj.stxtory.domain.dto.stock;

import lombok.Data;

import java.util.List;

@Data
public class StockInfo {
    private String name; // 종목명
    private String code; // 종목코드
    private int totalPage; // 네이버에서 가져올 전체 페이지
    private List<StockPriceInfo> prices; // 가격정보 리스트
}
