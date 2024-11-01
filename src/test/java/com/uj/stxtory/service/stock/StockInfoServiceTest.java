package com.uj.stxtory.service.stock;

import org.junit.jupiter.api.Test;

import java.util.Objects;

class StockInfoServiceTest {
    @Test
    public void getStockMarketIdentifierTest() {
        StockInfoService stockInfoService = new StockInfoService();
        assert Objects.equals(stockInfoService.getStockMarketIdentifier("093510"), false);
        assert Objects.equals(stockInfoService.getStockMarketIdentifier("055550"), true);
    }
}