package com.uj.stxtory.domain.dto.usstock;

import com.uj.stxtory.domain.dto.deal.DealInfo;
import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.dto.deal.DealPrice;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class USStockModel extends DealInfo {

    private final int SEARCH_DAYS; // 6개월
    private final String key;

    public USStockModel(int daySize, String key) {
        this.SEARCH_DAYS = daySize;
        this.key = key;
    }

    @Override
    public List<DealItem> getAll() {
        return USStockInfo.getCompanyInfo();
    }

    @Override
    public List<DealPrice> getPrice(DealItem item, int page) {
        return USStockInfo.getPriceInfoByPage(item.getCode(), SEARCH_DAYS, key);
    }
}
