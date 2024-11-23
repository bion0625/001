package com.uj.stxtory.domain.dto.UPbit;

import com.uj.stxtory.domain.dto.deal.DealInfo;
import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.dto.deal.DealPrice;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class UPbitModel extends DealInfo {

    int SEARCH_DAYS; // 6개월

    public UPbitModel(int daySize) {
        this.SEARCH_DAYS = daySize;
    }

    @Override
    public List<DealItem> getAll() {
        List<DealItem> coins = UPbitInfo.getAll();
        //원화 기준만 취급
        return coins.stream()
                .filter(c -> c.getCode().contains("KRW"))
                .collect(Collectors.toList());
    }

    @Override
    public List<DealPrice> getPrice(DealItem item, int page) {
        return UPbitInfo.getPriceInfoByDay(item.getCode(), SEARCH_DAYS);
    }
}
