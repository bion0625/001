package com.uj.stxtory.service.deal.notify;

import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.dto.deal.DealModel;
import com.uj.stxtory.domain.dto.deal.DealPrice;
import com.uj.stxtory.domain.dto.deal.DealSettingsInfo;
import com.uj.stxtory.domain.dto.stock.DividendStockInfo;
import com.uj.stxtory.domain.dto.stock.StockInfo;
import com.uj.stxtory.domain.dto.stock.StockModel;
import com.uj.stxtory.domain.dto.stock.StockPriceInfo;
import com.uj.stxtory.domain.entity.DividendStock;
import com.uj.stxtory.domain.entity.Stock;
import com.uj.stxtory.domain.entity.StockHistory;
import com.uj.stxtory.repository.DividendStockRepository;
import com.uj.stxtory.repository.StockHistoryRepository;
import com.uj.stxtory.repository.StockRepository;
import com.uj.stxtory.service.DealSettingsService;
import com.uj.stxtory.service.deal.DealNotifyService;
import com.uj.stxtory.service.deal.calculate.CalculateStockService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class StockNotifyService implements DealNotifyService {

  private static final String SETTING_NAME = "stock";

  private final StockRepository stockRepository;
  private final StockHistoryRepository stockHistoryRepository;
  private final DealSettingsService dealSettingsService;
  private final CalculateStockService calculateStockService;
  private final DividendStockRepository dividendStockRepository;

  public StockNotifyService(
      StockRepository stockRepository,
      DealSettingsService dealSettingsService,
      CalculateStockService calculStockService,
      StockHistoryRepository stockHistoryRepository,
      DividendStockRepository dividendStockRepository) {
    this.stockRepository = stockRepository;
    this.dealSettingsService = dealSettingsService;
    this.calculateStockService = calculStockService;
    this.stockHistoryRepository = stockHistoryRepository;
    this.dividendStockRepository = dividendStockRepository;
  }

  public List<StockInfo> getSaved() {
    return callSaved().stream().map(StockInfo::fromEntity).toList();
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
        .toList();
  }

  @Async
  @Override
  public void save() {
    List<Stock> saved = callSaved();

    DealSettingsInfo settings = dealSettingsService.getByName(SETTING_NAME);
    StockModel stockModel = new StockModel(settings.getHighestPriceReferenceDays());

    List<DealItem> saveItems =
        stockModel.calculateByThreeDaysByPageForSaveByDatabase(
            (double) settings.getExpectedLowPercentage(),
            (double) settings.getExpectedHighPercentage(),
            getPricesMap(stockModel, settings),
            settings.isVolumeCheck());

    List<Stock> save =
        saveItems.stream()
            .filter(item -> saved.stream().noneMatch(s -> s.getCode().equals(item.getCode())))
            .map(
                item ->
                    (Stock)
                        item.toEntity(
                            1 + ((double) settings.getExpectedHighPercentage() / 100),
                            1 + ((double) settings.getExpectedLowPercentage() / 100)))
            .toList();
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
  public DealModel update() {
    List<Stock> saved = callSaved();

    List<DealItem> items = saved.stream().map(StockInfo::fromEntity).collect(Collectors.toList());

    DealSettingsInfo settings = dealSettingsService.getByName(SETTING_NAME);

    DealModel model = new StockModel(settings.getHighestPriceReferenceDays());

    if (saved.isEmpty()) return model;
    model.calculateForTodayUpdateByDatabase(
        items,
        getPricesMap(items, model, settings),
        1 + ((double) settings.getExpectedHighPercentage() / 100),
        1 + ((double) settings.getExpectedLowPercentage() / 100));
    List<DealItem> updateItems = model.getNowItems();
    List<DealItem> deleteItems = model.getDeleteItems();

    update(saved, updateItems);
    delete(saved, deleteItems);

    return model;
  }

  private Map<String, List<DealPrice>> getPricesMap(
      List<DealItem> items, DealModel model, DealSettingsInfo settings) {
    Map<String, List<DealPrice>> pricesMap = new HashMap<>();
    items.forEach(
        item -> {
          List<StockPriceInfo> historyPrices =
              stockHistoryRepository.findByCode(item.getCode()).stream()
                  .map(
                      history ->
                          new StockPriceInfo(
                              Date.from(
                                  history
                                      .getCreatedAt()
                                      .atZone(ZoneId.systemDefault())
                                      .toInstant()),
                              history.getClose(),
                              history.getDiff(),
                              history.getOpen(),
                              history.getHigh(),
                              history.getLow(),
                              history.getVolume()))
                  .toList();
          ArrayList<DealPrice> prices = new ArrayList<>(historyPrices);
          prices.addAll(model.getPrice(item, 1));
          pricesMap.put(
              item.getCode(),
              prices.stream()
                  .filter(p -> p.getDate() != null)
                  .distinct()
                  .sorted((prev, curr) -> curr.getDate().compareTo(prev.getDate()))
                  .limit(settings.getHighestPriceReferenceDays())
                  .toList());
        });

    return pricesMap;
  }

  private Map<String, List<DealPrice>> getPricesMap(DealModel model, DealSettingsInfo settings) {
    Map<String, List<DealPrice>> pricesMap = new HashMap<>();

    Map<String, List<StockHistory>> historyMap =
        stockHistoryRepository.findByCreatedAtAfter(LocalDateTime.now().minusYears(1)).stream()
            .collect(Collectors.groupingBy(StockHistory::getCode));
    historyMap.forEach(
        (code, histories) -> {
          List<StockPriceInfo> historyPrices =
              histories.stream()
                  .map(
                      history ->
                          new StockPriceInfo(
                              Date.from(
                                  history
                                      .getCreatedAt()
                                      .atZone(ZoneId.systemDefault())
                                      .toInstant()),
                              history.getClose(),
                              history.getDiff(),
                              history.getOpen(),
                              history.getHigh(),
                              history.getLow(),
                              history.getVolume()))
                  .toList();
          ArrayList<DealPrice> prices = new ArrayList<>(historyPrices);
          StockInfo item = new StockInfo();
          item.setCode(code);
          prices.addAll(model.getPrice(item, 1));
          pricesMap.put(
              item.getCode(),
              prices.stream()
                  .filter(p -> p.getDate() != null)
                  .distinct()
                  .sorted((prev, curr) -> curr.getDate().compareTo(prev.getDate()))
                  .limit(settings.getHighestPriceReferenceDays())
                  .toList());
        });

    return pricesMap;
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

  @Override
  @Async
  public void saveHistory() {
    DealSettingsInfo settings = dealSettingsService.getByName(SETTING_NAME);
    StockModel stockModel = new StockModel(settings.getHighestPriceReferenceDays());

    // 저장로직
    stockModel
        .getAll()
        .forEach(info -> calculateStockService.savePriceHistoryWithLabel(info, stockModel));
  }

  public List<DividendStockInfo> getSavedDividendStocks() {
    return dividendStockRepository.findAllByDeletedAtIsNullOrderByDividendRateDesc().stream()
        .map(DividendStockInfo::fromEntity)
        // 추후 배당락일과 지급일이 설정된 종목이 우선 보여야 할 때 수정(현재 크롤링해서 가져오는 배당락일과 지급일이 유효하지 않음)
        //        .sorted(
        //            Comparator.comparing(
        //                    (DividendStockInfo s) -> s.getExDivDate() != null || s.getPayDate() !=
        // null)
        //                .reversed()
        //                .thenComparing(
        //                    DividendStockInfo::getDividendRate,
        //                    Comparator.nullsLast(Comparator.reverseOrder())))
        .toList();
  }

  @Async
  public void saveDividendStocks() {
    List<DividendStock> items = DividendStockInfo.getDividendStocks();

    // 기존 히스토리 있으면 삭제
    items.forEach(
        item ->
            dividendStockRepository
                .findByCodeAndDeletedAtIsNull(item.getCode())
                .ifPresent(stock -> stock.setDeletedAt(LocalDateTime.now())));

    // 저장
    dividendStockRepository.saveAll(items);
  }
}
