package com.uj.stxtory.domain.dto.stock;

import com.uj.stxtory.domain.dto.deal.DealInfo;
import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.dto.deal.DealPrice;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class StockModel extends DealInfo {

    int SEARCH_PAGE; // 6개월

    public StockModel(int daySize) {
        this.SEARCH_PAGE = daySize / 10;
    }

    @Override
    public int getPage() {
        return this.SEARCH_PAGE;
    }

    @Override
    public boolean isUsePage() {
        return true;
    }

    @Override
    public boolean isUseSize() {
        return true;
    }

    @Override
    public boolean useParallel() {
        return true;
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
    public boolean CustomCheckForDelete(DealItem item) {
        return StockInfo.isIdentifier(item.getCode());
    }
}
