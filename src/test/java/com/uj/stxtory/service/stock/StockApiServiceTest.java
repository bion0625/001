package com.uj.stxtory.service.stock;

import org.junit.jupiter.api.Test;

import java.util.Objects;

class StockApiServiceTest {
    @Test
    public void getStockMarketIdentifierTest() {
        StockApiService stockApiService = new StockApiService();
        assert Objects.equals(stockApiService.getStockMarketIdentifier("093510"), false);
        assert Objects.equals(stockApiService.getStockMarketIdentifier("055550"), true);
    }
}