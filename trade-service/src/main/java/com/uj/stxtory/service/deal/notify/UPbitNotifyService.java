package com.uj.stxtory.service.deal.notify;

import com.uj.stxtory.config.DealDaysConfig;
import com.uj.stxtory.domain.dto.deal.DealInfo;
import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.dto.upbit.UPbitInfo;
import com.uj.stxtory.domain.dto.upbit.UPbitModel;
import com.uj.stxtory.domain.entity.UPbit;
import com.uj.stxtory.repository.UPbitRepository;
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
public class UPbitNotifyService implements DealNotifyService {

    @Autowired
    private UPbitRepository uPbitRepository;

    @Autowired
    private DealDaysConfig dealDaysConfig;

    public List<UPbitInfo> getSaved() {
        return callSaved().stream().map(UPbitInfo::fromEntity).collect(Collectors.toList());
    }

    private List<UPbit> callSaved() { // 목표가와 현재가가 비율 상으로 가장 가까운 순
        return uPbitRepository.findAllByDeletedAtIsNullOrderByPricingReferenceDateDesc().stream()
                .filter(u -> u.getExpectedSellingPrice() != u.getMinimumSellingPrice())
                .sorted(Comparator.comparing(UPbit::getRenewalCnt).reversed()
                		.thenComparingDouble(u -> (u.getExpectedSellingPrice() - u.getTempPrice())/(u.getExpectedSellingPrice()-u.getMinimumSellingPrice())))
                .collect(Collectors.toList());
    }

    @Async
    @Override
    public void save() {
        List<UPbit> saved = callSaved();

        DealInfo model = new UPbitModel(dealDaysConfig.getUpbitBaseDays());

        List<DealItem> saveItems = model.calculateByThreeDaysByPageForSave();

        List<UPbit> save = saveItems.stream()
                .filter(item -> saved.stream().noneMatch(s -> s.getCode().equals(item.getCode())))
                .map(item -> (UPbit) item.toEntity())
                .collect(Collectors.toList());
        uPbitRepository.saveAll(save);
        
        List<UPbitInfo> deleteList = saved.stream()
        		.filter(s -> saveItems.stream().noneMatch(item -> item.getCode().equals(s.getCode())) && s.getRenewalCnt() == 0.0)
        		.map(UPbitInfo::fromEntity)
        		.collect(Collectors.toList());
        delete(saved, new ArrayList<>(deleteList));
    }

    @Override
    public DealInfo update() {
        List<UPbit> saved = callSaved();

        List<DealItem> items = saved.stream().map(UPbitInfo::fromEntity).collect(Collectors.toList());

        DealInfo model = new UPbitModel(dealDaysConfig.getUpbitBaseDays());
        if (saved.isEmpty()) return model;
        model.calculateForTodayUpdate(new ArrayList<>(items));
        List<DealItem> updateItems = model.getNowItems();
        List<DealItem> deleteItems = model.getDeleteItems();

        update(saved, updateItems);
        delete(saved, deleteItems);

        return model;
    }

    private void update(List<UPbit> saved, List<DealItem> updateItems) {
        saved.forEach(uPbit -> updateItems.stream()
                .filter(pItem -> pItem.getCode().equals(uPbit.getCode()))
                .findFirst().map(item -> {
                    uPbit.setPricingReferenceDate(item.getPricingReferenceDate());
                    uPbit.setExpectedSellingPrice(item.getExpectedSellingPrice());
                    uPbit.setMinimumSellingPrice(item.getMinimumSellingPrice());
                    uPbit.setRenewalCnt(item.getRenewalCnt());
                    uPbit.setTempPrice(item.getTempPrice());
                    uPbit.setSettingPrice(item.getSettingPrice());
                    uPbit.setUpdatedAt(LocalDateTime.now());
                    return uPbit;
                }));
    }
    
    private void delete(List<UPbit> saved, List<DealItem> deleteItems) {
    	saved.forEach(uPbit ->
                deleteItems.stream().filter(pItem -> pItem.getCode().equals(uPbit.getCode())).findFirst().map(item -> {
                    uPbit.setDeletedAt(LocalDateTime.now());
                    return uPbit;
                }));
    }
}
