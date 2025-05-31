package com.uj.stxtory.service.deal.notify;

import com.uj.stxtory.domain.dto.deal.DealInfo;
import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.dto.deal.DealPrice;
import com.uj.stxtory.domain.dto.deal.DealSettingsInfo;
import com.uj.stxtory.domain.dto.stock.StockInfo;
import com.uj.stxtory.domain.dto.stock.StockModel;
import com.uj.stxtory.domain.entity.Stock;
import com.uj.stxtory.domain.entity.StockHistory;
import com.uj.stxtory.domain.entity.StockHistoryLabel;
import com.uj.stxtory.repository.StockHistoryLabelRepository;
import com.uj.stxtory.repository.StockHistoryRepository;
import com.uj.stxtory.repository.StockRepository;
import com.uj.stxtory.service.DealSettingsService;
import com.uj.stxtory.service.deal.DealNotifyService;
import com.uj.stxtory.util.FormatUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class StockNotifyService implements DealNotifyService {

  private final StockRepository stockRepository;
  private final DealSettingsService dealSettingsService;
  private final StockHistoryLabelRepository stockHistoryLabelRepository;
  private final StockHistoryRepository stockHistoryRepository;

  public StockNotifyService(
      StockRepository stockRepository,
      DealSettingsService dealSettingsService,
      StockHistoryLabelRepository stockHistoryLabelRepository,
      StockHistoryRepository stockHistoryRepository) {
    this.stockRepository = stockRepository;
    this.dealSettingsService = dealSettingsService;
    this.stockHistoryLabelRepository = stockHistoryLabelRepository;
    this.stockHistoryRepository = stockHistoryRepository;
  }

  public List<StockInfo> getSaved() {
    return callSaved().stream().map(StockInfo::fromEntity).collect(Collectors.toList());
  }

  private List<Stock> callSaved() { // 목표가와 현재가가 비율 상으로 가장 가까운 순
    return stockRepository.findAllByDeletedAtIsNullOrderByPricingReferenceDateDesc().stream()
        .filter(s -> s.getExpectedSellingPrice() != s.getMinimumSellingPrice())
        .sorted(
            Comparator.comparing(Stock::getRenewalCnt)
                .reversed()
                .thenComparingDouble(
                    s ->
                        (s.getExpectedSellingPrice() - s.getTempPrice())
                            / (s.getExpectedSellingPrice() - s.getMinimumSellingPrice())))
        .collect(Collectors.toList());
  }

  @Async
  @Override
  public void save() {
    List<Stock> saved = callSaved();

    DealSettingsInfo settings = dealSettingsService.getByName("stock");
    StockModel stockModel = new StockModel(settings.getHighestPriceReferenceDays());

    List<DealItem> saveItems =
        stockModel.calculateByThreeDaysByPageForSave(
            1 + ((double) settings.getExpectedLowPercentage() / 100));

    List<Stock> save =
        saveItems.stream()
            .filter(item -> saved.stream().noneMatch(s -> s.getCode().equals(item.getCode())))
            .map(
                item ->
                    (Stock)
                        item.toEntity(
                            1 + ((double) settings.getExpectedHighPercentage() / 100),
                            1 + ((double) settings.getExpectedLowPercentage() / 100)))
            .collect(Collectors.toList());
    stockRepository.saveAll(save);

    List<StockInfo> deleteList =
        saved.stream()
            .filter(
                s ->
                    saveItems.stream().noneMatch(item -> item.getCode().equals(s.getCode()))
                        && s.getRenewalCnt() == 0.0)
            .map(StockInfo::fromEntity)
            .toList();
    delete(saved, new ArrayList<>(deleteList));
  }

  @Override
  public DealInfo update() {
    List<Stock> saved = callSaved();

    List<StockInfo> items = saved.stream().map(StockInfo::fromEntity).toList();

    DealSettingsInfo settings = dealSettingsService.getByName("stock");
    DealInfo model = new StockModel(settings.getHighestPriceReferenceDays());
    if (saved.isEmpty()) return model;
    model.calculateForTodayUpdate(
        new ArrayList<>(items),
        1 + ((double) settings.getExpectedHighPercentage() / 100),
        1 + ((double) settings.getExpectedLowPercentage() / 100));
    List<DealItem> updateItems = model.getNowItems();
    List<DealItem> deleteItems = model.getDeleteItems();

    update(saved, updateItems);
    delete(saved, deleteItems);

    return model;
  }

  private void update(List<Stock> saved, List<DealItem> updateItems) {
    saved.forEach(
        stock ->
            updateItems.stream()
                .filter(pItem -> pItem.getCode().equals(stock.getCode()))
                .findFirst()
                .map(
                    item -> {
                      stock.setPricingReferenceDate(item.getPricingReferenceDate());
                      stock.setExpectedSellingPrice(item.getExpectedSellingPrice());
                      stock.setMinimumSellingPrice(item.getMinimumSellingPrice());
                      stock.setRenewalCnt(item.getRenewalCnt());
                      stock.setTempPrice(item.getTempPrice());
                      stock.setSettingPrice(item.getSettingPrice());
                      stock.setUpdatedAt(LocalDateTime.now());
                      return stock;
                    }));
  }

  private void delete(List<Stock> saved, List<DealItem> deleteItems) {
    saved.forEach(
        stock ->
            deleteItems.stream()
                .filter(pItem -> pItem.getCode().equals(stock.getCode()))
                .findFirst()
                .map(
                    item -> {
                      stock.setDeletedAt(LocalDateTime.now());
                      return stock;
                    }));
  }

  @Async
  public void saveHistory() {
    DealSettingsInfo settings = dealSettingsService.getByName("stock");
    StockModel stockModel = new StockModel(settings.getHighestPriceReferenceDays());

    // 저장로직
    stockModel
        .getAll()
        .forEach(
            info ->
                stockHistoryLabelRepository
                    .findByCodeAndName(info.getCode(), info.getName())
                    .map(
                        label -> {
                          // 추가 저장은 label의 updatedAt 다음날부터
                          stockModel.getPriceByPage(info, 1, 130).stream()
                              .filter(
                                  p ->
                                      FormatUtil.dateToLocalDateTime(p.getDate()) != null
                                          && FormatUtil.dateToLocalDateTime(p.getDate())
                                              .isAfter(label.getUpdatedAt()))
                              .forEach(p -> savePriceHistory(info, p));
                          label.setUpdatedAt(LocalDateTime.now());
                          return label;
                        })
                    .orElseGet(
                        () -> {
                          // 새로 저장은 1년
                          stockModel
                              .getPriceByPage(info, 1, 130)
                              .forEach(p -> savePriceHistory(info, p));
                          StockHistoryLabel entity =
                              new StockHistoryLabel(info.getCode(), info.getName());
                          entity.setUpdatedAt(LocalDateTime.now());
                          return stockHistoryLabelRepository.save(entity);
                        }));
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  protected void savePriceHistory(DealItem info, DealPrice p) {
    // 저장 날짜를 타겟 날짜로
    Optional.ofNullable(p.getDate())
        .map(
            date -> {
              LocalDateTime createdAt = FormatUtil.dateToLocalDateTime(date);
              return stockHistoryRepository
                  .findByCodeAndNameAndCreatedAt(info.getCode(), info.getName(), createdAt)
                  .orElseGet(
                      () -> {
                        StockHistory history =
                            StockHistory.builder()
                                .name(info.getName())
                                .code(info.getCode())
                                .low(p.getLow())
                                .high(p.getHigh())
                                .close(p.getClose())
                                .diff(p.getDiff())
                                .volume(p.getVolume())
                                .build();
                        history.setCreatedAt(createdAt);
                        return stockHistoryRepository.save(history);
                      });
            });
  }
}
