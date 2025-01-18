package com.uj.stxtory.service.deal.notify;

import com.uj.stxtory.config.DealDaysConfig;
import com.uj.stxtory.domain.dto.deal.DealInfo;
import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.dto.usstock.USStockInfo;
import com.uj.stxtory.domain.dto.usstock.USStockModel;
import com.uj.stxtory.domain.entity.USStock;
import com.uj.stxtory.repository.USStockRepository;
import com.uj.stxtory.service.deal.DealNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class USStockNotifyService implements DealNotifyService {

    @Autowired
    private USStockRepository usStockRepository;

    @Autowired
    private DealDaysConfig dealDaysConfig;
    @Value("${usstock.api.key}")
    String key;

    public List<USStockInfo> getSaved() {
        return callSaved().stream().map(USStockInfo::fromEntity).collect(Collectors.toList());
    }

    private List<USStock> callSaved() { // 목표가와 현재가가 비율 상으로 가장 가까운 순
        return usStockRepository.findAllByDeletedAtIsNullOrderByPricingReferenceDateDesc().stream()
                .filter(s -> s.getExpectedSellingPrice() != s.getMinimumSellingPrice())
                .sorted(Comparator.comparing(USStock::getRenewalCnt).reversed()
                		.thenComparingDouble(s -> (s.getExpectedSellingPrice() - s.getTempPrice())/(s.getExpectedSellingPrice()-s.getMinimumSellingPrice())))
                .collect(Collectors.toList());
    }

    @Async
    @Override
    public void save() {
        List<USStock> saved = callSaved();

        USStockModel usStockModel = new USStockModel(dealDaysConfig.getBaseDays(), key);

        List<DealItem> saveItems = usStockModel.calculateByThreeDaysByPageForSave();

        List<USStock> save = saveItems.stream()
                .filter(item -> saved.stream().noneMatch(s -> s.getCode().equals(item.getCode())))
                .map(item -> (USStock) item.toEntity())
                .collect(Collectors.toList());
        usStockRepository.saveAll(save);
        
        List<USStockInfo> deleteList = saved.stream()
        		.filter(s -> saveItems.stream().noneMatch(item -> item.getCode().equals(s.getCode())) && s.getRenewalCnt() == 0.0)
        		.map(USStockInfo::fromEntity)
        		.collect(Collectors.toList());
        delete(saved, new ArrayList<>(deleteList));
    }

    @Override
    public DealInfo update() {
        List<USStock> saved = callSaved();

        List<USStockInfo> items = saved.stream().map(USStockInfo::fromEntity).collect(Collectors.toList());

        DealInfo model = new USStockModel(dealDaysConfig.getBaseDays(), key);
        if (saved.isEmpty()) return model;
        model.calculateForTodayUpdate(new ArrayList<>(items));
        List<DealItem> updateItems = model.getNowItems();
        List<DealItem> deleteItems = model.getDeleteItems();

        update(saved, updateItems);
        delete(saved, deleteItems);

        return model;
    }

    private void update(List<USStock> saved, List<DealItem> updateItems) {
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
    
    private void delete(List<USStock> saved, List<DealItem> deleteItems) {
    	saved.forEach(stock ->
                deleteItems.stream().filter(pItem -> pItem.getCode().equals(stock.getCode())).findFirst().map(item -> {
                    stock.setDeletedAt(LocalDateTime.now());
                    return stock;
                }));
    }
}
