package com.uj.stxtory.domain.dto.stock;

import com.uj.stxtory.domain.dto.deal.DealInfo;
import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.dto.deal.DealPrice;
import com.uj.stxtory.domain.entity.Stock;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class StockModel extends DealInfo {
    @Setter
    List<Stock> oldStock;

    int SEARCH_PAGE = 13; // 6개월

    public StockModel(int pageSize) {
        this.SEARCH_PAGE = pageSize;
    }

    @Override
    public int getPage() {
        return this.SEARCH_PAGE;
    }

    @Override
    public List<DealItem> getAll() {
        return StockInfo.getCompanyInfo().stream()
                .map(StockInfo::toDealItem).collect(Collectors.toList());
    }

    @Override
    public List<DealPrice> getPrice(DealItem item, int page) {
        return StockInfo.getPriceInfo(item.getCode(), page).stream()
                .map(StockPriceInfo::toDealPrice).collect(Collectors.toList());
    }

    @Override
    public List<DealPrice> getPriceByPage(DealItem item, int from, int to) {
        return StockInfo.getPriceInfoByPage(item.getCode(), from, to).stream()
                .map(StockPriceInfo::toDealPrice).collect(Collectors.toList());
    }

    @Override
    public List<DealItem> getOld() {
        return this.oldStock.stream()
                .map(DealItem::fromStock).collect(Collectors.toList());
    }

    // 코스피나 코스닥이 아니면 삭제 후 제외
    @Override
    public boolean CustomCheck(DealItem item) {
        return !StockInfo.getStockMarketIdentifier(item.getCode());
    }
}
