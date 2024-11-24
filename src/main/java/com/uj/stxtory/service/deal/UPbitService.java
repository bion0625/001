package com.uj.stxtory.service.deal;

import com.uj.stxtory.config.DealDaysConfig;
import com.uj.stxtory.domain.dto.upbit.UPbitInfo;
import com.uj.stxtory.domain.dto.upbit.UPbitModel;
import com.uj.stxtory.domain.dto.deal.DealInfo;
import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.entity.UPbit;
import com.uj.stxtory.repository.UPbitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class UPbitService {

    @Autowired
    private UPbitRepository uPbitRepository;

    @Autowired
    private DealDaysConfig dealDaysConfig;

    public List<UPbitInfo> getSaved() {
        return callSaved().stream().map(UPbitInfo::fromEntity).collect(Collectors.toList());
    }

    private List<UPbit> callSaved() { // 목표가와 현재가가 비율 상으로 가장 가까운 순
        return uPbitRepository.findAllByDeletedAtIsNull().stream()
                .sorted((u1, u2) -> Double.compare(
                        ((u2.getExpectedSellingPrice() - u2.getTempPrice())/u2.getExpectedSellingPrice()),
                        ((u1.getExpectedSellingPrice() - u1.getTempPrice())/u1.getExpectedSellingPrice())
                )).collect(Collectors.toList());
    }

    public void save() {
        List<UPbit> saved = callSaved();

        DealInfo model = new UPbitModel(dealDaysConfig.getBaseDays());

        List<DealItem> saveItems = model.calculateByThreeDaysByPageForSave();

        List<UPbit> save = saveItems.stream()
                .filter(item -> saved.stream().noneMatch(s -> s.getCode().equals(item.getCode())))
                .map(item -> (UPbit) item.toEntity())
                .collect(Collectors.toList());
        uPbitRepository.saveAll(save);
    }

    public DealInfo update() {
        List<UPbit> saved = callSaved();

        List<DealItem> items = saved.stream().map(UPbitInfo::fromEntity).collect(Collectors.toList());

        DealInfo model = new UPbitModel(dealDaysConfig.getBaseDays());
        if (saved.size() == 0) return model;
        model.calculateForTodayUpdate(new ArrayList<>(items));
        List<DealItem> updateItems = model.getUpdateItems();
        List<DealItem> deleteItems = model.getDeleteItems();

        update(saved, updateItems, deleteItems);

        return model;
    }

    private void update(List<UPbit> saved, List<DealItem> updateItems, List<DealItem> deleteItems) {
        saved.forEach(uPbit -> {
            updateItems.stream()
                    .filter(pItem -> pItem.getCode().equals(uPbit.getCode()))
                    .findFirst().map(item -> {
                        uPbit.setExpectedSellingPrice(item.getExpectedSellingPrice());
                        uPbit.setMinimumSellingPrice(item.getMinimumSellingPrice());
                        uPbit.setRenewalCnt(item.getRenewalCnt());
                        uPbit.setTempPrice(item.getTempPrice());
                        uPbit.setUpdatedAt(LocalDateTime.now());
                        return uPbit;
                    });
            deleteItems.stream().filter(pItem -> pItem.getCode().equals(uPbit.getCode())).findFirst().map(item -> {
                uPbit.setDeletedAt(LocalDateTime.now());
                return uPbit;
            });
        });
    }
}
