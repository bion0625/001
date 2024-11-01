package com.uj.stxtory.service.stock;

import com.uj.stxtory.domain.dto.stock.StockInfo;
import com.uj.stxtory.domain.dto.stock.StockPriceInfo;
import com.uj.stxtory.domain.entity.Stock;
import com.uj.stxtory.repository.StockRepository;
import com.uj.stxtory.service.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/*
 * 1) 3일 연달아 가격 상승이 아니면 제외
 * 2) 오늘이 거래량 최고면 제외
 * 3) 기준 날짜 대비 신고가 아니면 제외
 * 4) 당일 diff가 5% ~ 15% 내에 있지 않으면 제외
 * 5) 고점 대비 당일 최저가가 5% 미만이면 제외
 */
@Transactional
@Slf4j
@Service
public class TreeDayPriceService {
    @Autowired
    StockInfoService stockInfoService;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    MailService mailService;

    public List<Stock> getAll() {
        List<Stock> all = stockRepository.findAllByDeletedAtIsNull();
        // 갱신된 횟수 기준으로 내림차순 정리
        all.sort((o1, o2) -> o2.getRenewalCnt() - o1.getRenewalCnt());
        return all;
    }

//    public List<Stock> getDeleteByDate() {
//        return stockRepository.findAllByDeletedAtAfter(LocalDateTime.now().minusMinutes(15));
//    }

    public void saveNewByToday() {
        List<Stock> all = getAll();
        List<String> codes = all.parallelStream().map(Stock::getCode).collect(Collectors.toList());
        List<Stock> today = new ArrayList<>();
        try {
            today = start()
                    .parallelStream().map(StockInfo::toEntity).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Stock stock : today) {
            if (!codes.contains(stock.getCode())) {
                stockRepository.save(stock);
            }
        }
    }

    public void renewalUpdateByToday() {
        List<Stock> all = getAll();
        all.parallelStream()
                .filter(stock -> {
                    // 코스피나 코스닥이 아니면 삭제 후 제외
                    if (!stockInfoService.getStockMarketIdentifier(stock.getCode())) {
                        stock.setDeletedAt(LocalDateTime.now());
                        return false;
                    }
                    return true;
                }).forEach(stock -> {
                    List<StockPriceInfo> prices = stockInfoService.getPriceInfo(stock.getCode(), 1);
                    // 거래량이 0이면 하루 전으로 계산
                    int lastDayIndex = prices.get(0).getVolume() == 0 ? 1 : 0;
                    // 마지막 가격
                    StockPriceInfo price = prices.get(lastDayIndex);

                    // 당일 현재(종)가가 기대 매도 가격보다 높으면 하한 가격 및 기대 가격 갱신
                    while (price.getClose() != 0 && stock.getExpectedSellingPrice() != 0
                            && price.getClose() >= stock.getExpectedSellingPrice()) stock.sellingPriceUpdate(price.getDate());
                    // 현재 종가(현재가)가 하한 매도 가격 대비 같거나 낮으면 삭제
                    if (price.getClose() <= stock.getMinimumSellingPrice()) stock.setDeletedAt(LocalDateTime.now());
                });
        List<Stock> deleted = all.parallelStream().filter(s -> s.getDeletedAt() != null).collect(Collectors.toList());
        if (deleted.isEmpty()) return;
        mailService.treeDaysMailSend(deleted, " - 매도 종목");
    }

    public List<StockInfo> start() {
        // 신고가 개월 수 (페이지 계산식 항)
        // int MONTH = 6;
        /*
         * 13 페이지 6개월
         * 1페이지 10일 (2주)
         * 2페이지 1개월
         * 4페이지 2개월
         * 6페이지 3개월
         * ..
         */
        // int SEARCH_PAGE = 2 * MONTH;
        int SEARCH_PAGE = 13; // 6개월

        List<StockInfo> stocks = stockInfoService.getCompanyInfo();

        return filterByThreeDay(stocks, SEARCH_PAGE);
    }

    private List<StockInfo> filterByThreeDay(List<StockInfo> stocks, int SEARCH_PAGE) {
        // 로그시작
        log.info("save log start!");

        return stocks.parallelStream()
                .filter(stock -> {
                    // 부하를 방지하기 위해 일단 1페이지만 확인 후 신고가 설정할 때 다시 구하기
                    List<StockPriceInfo> prices = stockInfoService.getPriceInfo(stock.getCode(), 1);

                    // 거래량이 0이면 하루 전으로 계산
                    int lastdayIndex = prices.get(0).getVolume() == 0 ? 1 : 0;

                    // 최근 3일 중 마지막일이 최고 거래량이면 제외
                    StockPriceInfo checkPrice = prices.subList(lastdayIndex, (lastdayIndex + 3)).parallelStream().reduce((p, c) -> p.getVolume() > c.getVolume() ? p : c).orElse(null);
                    if (checkPrice != null && prices.get(0).getVolume() == checkPrice.getVolume()) return false;

                    // 마지막일 diff가 5% ~ 15% 내에 있지 않으면 제외
                    double diffPercent = (double) (prices.get(lastdayIndex).getDiff() * 100) / (double) prices.get(lastdayIndex + 1).getClose();
                    if (prices.get(lastdayIndex).getDiff() < 0 || (diffPercent < 5 || diffPercent > 15)) return false;

                    // 3일 연달아 가격 상승이 아니면 제외
                    if(prices.get(lastdayIndex).getHigh() <= prices.get(lastdayIndex + 1).getHigh() || prices.get(lastdayIndex + 1).getHigh() <= prices.get(lastdayIndex + 2).getHigh()
                            || prices.get(lastdayIndex).getLow() <= prices.get(lastdayIndex + 1).getLow() || prices.get(lastdayIndex + 1).getLow() <= prices.get(lastdayIndex + 2).getLow()) {
                        return false;
                    }

                    // 고점 대비 5% 미만이면 제외 - 현재가(종가) 기준
                    if (prices.get(lastdayIndex).getClose() < (Math.round(prices.get(lastdayIndex).getHigh() * 0.95))) return false;

                    // 부하를 방지하기 위해 신고가 설정할 때 다시 구하기
                    prices = stockInfoService.getPriceInfoByPage(stock.getCode(), 1, SEARCH_PAGE);

                    // 조회 기간(6개월) 중 신고가가 아니면 제외
                    checkPrice = prices.parallelStream().max(Comparator.comparingLong(StockPriceInfo::getHigh)).orElse(null);
                    if(checkPrice == null || prices.get(lastdayIndex).getHigh() != checkPrice.getHigh()) return false;

                    // 리스트에 저장
                    stock.setPrices(prices);

                    return true;
                })
                // 로그
                .peek(stock -> log.info(String.format("\tsuccess:\t%s", stock.getName())))
                .collect(Collectors.toList());
    }
}
