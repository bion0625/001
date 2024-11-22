package com.uj.stxtory.domain.dto.stock;

import com.uj.stxtory.domain.dto.deal.DealInfo;
import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.dto.deal.DealPrice;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class StockModel extends DealInfo {

    // todo del
    public static void main(String[] args) {
        StockModel model = new StockModel(1);
        List<DealItem> dealItems = model.calculateByThreeDaysByPageForSave();
        System.out.println(dealItems);
    }

    int SEARCH_PAGE; // 6개월

    public StockModel(int pageSize) {
        this.SEARCH_PAGE = pageSize;
    }

    @Override
    public int getPage() {
        return this.SEARCH_PAGE;
    }

    @Override
    public List<DealItem> getAll() {
        return StockInfo.getCompanyInfo();
    }

    @Override
    public List<DealPrice> getPrice(DealItem item, int page) {
        return StockInfo.getPriceInfo(item.getCode(), page);
    }

    @Override
    public List<DealPrice> getPriceByPage(DealItem item, int from, int to) {
        return StockInfo.getPriceInfoByPage(item.getCode(), from, to);
    }

    // 코스피나 코스닥이 아니면 삭제 후 제외
    @Override
    public boolean CustomCheck(DealItem item) {
        return !StockInfo.getStockMarketIdentifier(item.getCode());
    }
}
