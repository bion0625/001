package com.uj.stxtory.service.deal.notify;

import com.uj.stxtory.config.DealDaysConfig;
import com.uj.stxtory.domain.dto.deal.DealInfo;
import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.dto.stock.StockInfo;
import com.uj.stxtory.domain.dto.stock.StockModel;
import com.uj.stxtory.domain.entity.Stock;
import com.uj.stxtory.repository.StockRepository;
import com.uj.stxtory.service.deal.DealNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class StockNotifyService implements DealNotifyService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private DealDaysConfig dealDaysConfig;

    public List<StockInfo> getSaved() {
        return callSaved().stream().map(StockInfo::fromEntity).collect(Collectors.toList());
    }

    private List<Stock> callSaved() { // 목표가와 현재가가 비율 상으로 가장 가까운 순
        return stockRepository.findAllByDeletedAtIsNullOrderByPricingReferenceDateDesc().stream()
                .filter(s -> s.getExpectedSellingPrice() != s.getMinimumSellingPrice())
                .sorted(Comparator.comparing(Stock::getRenewalCnt).reversed()
                		.thenComparingDouble(s -> (s.getExpectedSellingPrice() - s.getTempPrice())/(s.getExpectedSellingPrice()-s.getMinimumSellingPrice())))
                .collect(Collectors.toList());
    }

    @Async
    @Override
    public void save() {
        List<Stock> saved = callSaved();

        StockModel stockModel = new StockModel(dealDaysConfig.getBaseDays());

        List<DealItem> saveItems = stockModel.calculateByThreeDaysByPageForSave();

        List<Stock> save = saveItems.stream()
                .filter(item -> saved.stream().noneMatch(s -> s.getCode().equals(item.getCode())))
                .map(item -> (Stock) item.toEntity())
                .collect(Collectors.toList());
        stockRepository.saveAll(save);
        
        List<StockInfo> deleteList = saved.stream()
        		.filter(s -> saveItems.stream().noneMatch(item -> item.getCode().equals(s.getCode())) && s.getRenewalCnt() == 0.0)
        		.map(StockInfo::fromEntity)
        		.collect(Collectors.toList());
        delete(saved, new ArrayList<>(deleteList));
    }

    @Override
    public DealInfo update() {
        List<Stock> saved = callSaved();

        List<StockInfo> items = saved.stream().map(StockInfo::fromEntity).collect(Collectors.toList());

        DealInfo model = new StockModel(dealDaysConfig.getBaseDays());
        if (saved.isEmpty()) return model;
        model.calculateForTodayUpdate(new ArrayList<>(items));
        List<DealItem> updateItems = model.getNowItems();
        List<DealItem> deleteItems = model.getDeleteItems();

        update(saved, updateItems);
        delete(saved, deleteItems);

        return model;
    }

    private void update(List<Stock> saved, List<DealItem> updateItems) {
        saved.forEach(stock ->
                updateItems.stream()
                        .filter(pItem -> pItem.getCode().equals(stock.getCode()))
                        .findFirst().map(item -> {
                            stock.setPricingReferenceDate(item.getPricingReferenceDate());
                            stock.setExpectedSellingPrice(item.getExpectedSellingPrice());
                            stock.setMinimumSellingPrice(item.getMinimumSellingPrice());
                            stock.setRenewalCnt(item.getRenewalCnt());
                            stock.setTempPrice(item.getTempPrice());
                            stock.setUpdatedAt(LocalDateTime.now());
                            return stock;
                        }));
    }
    
    private void delete(List<Stock> saved, List<DealItem> deleteItems) {
    	saved.forEach(stock ->
                deleteItems.stream().filter(pItem -> pItem.getCode().equals(stock.getCode())).findFirst().map(item -> {
                    stock.setDeletedAt(LocalDateTime.now());
                    return stock;
                }));
    }
}
