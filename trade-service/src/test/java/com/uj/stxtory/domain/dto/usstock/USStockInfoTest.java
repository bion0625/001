package com.uj.stxtory.domain.dto.usstock;

import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.dto.deal.DealPrice;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
class USStockInfoTest {
    @Value("${usstock.api.key}")
    String key;
    @Test
    void getCompanyInfo() {
        List<DealItem> stockInfos = USStockInfo.getCompanyInfo();

        for (DealItem stockInfo : stockInfos) {
            System.out.println(stockInfo);
        }
    }

    @Test
    void getPriceInfo() throws IOException {
        log.info("AAPL close: " + USStockInfo.getClose("AAPL").orElseThrow());
    }

    @Test
    void getPriceInfoByPage() {
        List<DealPrice> prices = USStockInfo.getPriceInfoByPage("AAPL", 100, key);
        prices.forEach(p -> log.info(p.toString()));
    }

}