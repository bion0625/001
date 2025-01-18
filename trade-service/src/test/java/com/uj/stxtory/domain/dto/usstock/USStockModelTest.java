package com.uj.stxtory.domain.dto.usstock;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
class USStockModelTest {
    @Value("${usstock.api.key}")
    String key;

    @Test
    void calculateByThreeDaysByPageForSave() {
        USStockModel model = new USStockModel(130, key);
        model.calculateByThreeDaysByPageForSave().forEach(item -> log.info("item: " + item.toString()));
    }

}