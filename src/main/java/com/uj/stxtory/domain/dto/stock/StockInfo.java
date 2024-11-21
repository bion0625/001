package com.uj.stxtory.domain.dto.stock;

import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.entity.Stock;
import com.uj.stxtory.util.FormatUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
public class StockInfo {
    private String name; // 종목명
    private String code; // 종목코드
    private int totalPage; // 네이버에서 가져올 전체 페이지
    private List<StockPriceInfo> prices; // 가격정보 리스트

    public Stock toEntity() {
        long high = prices.get(0).getHigh();
        long minimum = Math.round(high * 0.95);
        long expected = Math.round(high * 1.1);
        return new Stock(code, name, minimum, expected, minimum, expected);
    }

    public DealItem toDealItem() {
        long high = prices.get(0).getHigh();
        long minimum = Math.round(high * 0.95);
        long expected = Math.round(high * 1.1);
        return new DealItem(code, name, minimum, expected, LocalDateTime.now(), 0);
    }

    public static List<StockInfo> getCompanyInfo() { // 기본정보 가져오기
        List<StockInfo> stocks = new ArrayList<>();
        Document doc = null;
        try {
            doc = getDocumentByUrl("http://kind.krx.co.kr/corpgeneral/corpList.do?method=download&searchType=13");
        } catch (Exception e) {
            log.info("getCompanyInfo error");
        }
        Elements infoList = doc.select("tr");
        for (int i = 1; i < infoList.size(); i++) {
            Elements info = infoList.get(i).select("td");
            StockInfo stock = new StockInfo();
            stock.setName(info.get(0).text());
            stock.setCode(info.get(1).text());
            stocks.add(stock);
        }

        return stocks.parallelStream().filter(s -> getStockMarketIdentifier(s.getCode())).collect(Collectors.toList());
    }

    public static Boolean getStockMarketIdentifier (String code) {
        Document doc = null;
        try {
            doc = getDocumentByUrl(String.format("https://finance.naver.com/item/main.naver?code=%s", code));
        } catch (Exception e) {
            log.info("getStockMarketIdentifier error: " + code);
        }

        Elements kospiList = doc.select("img.kospi");

        for (Element img : kospiList) {
            String altText = img.attr("alt");
            if ("코스피".equals(altText)) return true;
        }

        Elements kosdaqList = doc.select("img.kosdaq");
        for (Element img : kosdaqList) {
            String altText = img.attr("alt");
            if ("코스닥".equals(altText)) return true;
        }

        return false;
    }

    public static List<StockPriceInfo> getPriceInfoByPage(String code, int from, int to) {
        List<StockPriceInfo> prices = new ArrayList<>();
        for (int page = from; page <= to; page++) {
            prices.addAll(getPriceInfo(code, page));
        }
        return prices;
    }

    public static List<StockPriceInfo> getPriceInfo(String code, int page) { // 종목 및 페이지로 가격 정보 가져오기
        Document doc = null;
        try {
            doc = getDocumentByUrl(String.format("http://finance.naver.com/item/sise_day.nhn?code=%s&page=%d", code, page));
        } catch (Exception e) {
            log.info("getPriceInfo error: " + code + ", " + page);
        }

        Elements infoList = doc.select("tr");

        List<StockPriceInfo> prices = new ArrayList<>();

        for (int i = 2; i < infoList.size() - 2; i++) {
            if (i >= 7 && i <= 9) {
                continue;
            }
            StockPriceInfo price = new StockPriceInfo();

            Elements info = infoList.get(i).select("td");
            FormatUtil formatUtil = new FormatUtil();
            price.setDate(formatUtil.stringToDate(info.get(0).text()));
            price.setClose(formatUtil.stringToLong(info.get(1).text()));
            price.setOpen(formatUtil.stringToLong(info.get(3).text()));
            price.setHigh(formatUtil.stringToLong(info.get(4).text()));
            price.setLow(formatUtil.stringToLong(info.get(5).text()));
            price.setVolume(formatUtil.stringToLong(info.get(6).text()));

            Boolean minusDiffFlag = info.get(2).text().contains("하한가") || info.get(2).text().contains("하락");
            price.setDiff(formatUtil.stringToLong(info.get(2).text()
                    .replaceAll("상한가","")
                    .replaceAll("하한가","")
                    .replaceAll("상승","")
                    .replaceAll("하락","")
                    .replaceAll("보합","")
                    .trim()));
            if (minusDiffFlag) {
                price.setDiff(price.getDiff() * -1);
            }

            prices.add(price);
        }

        return prices;
    }

//    public static int getPriceTotalPage(String code) throws IOException { // 가격 정보 가져올 때, 전체 페이지 가져오기
//        Document doc = getDocumentByUrl(String.format("http://finance.naver.com/item/sise_day.nhn?code=%s", code));
//
//        Element pgRR = doc.select(".pgRR a").get(0);
//        String href = pgRR.attr("href");
//        int totalPage = Integer.valueOf(href.substring(href.indexOf("&page=") + 6).trim());
//        return totalPage;
//    }

    private static Document getDocumentByUrl(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .ignoreContentType(true)
                .timeout(10000)
                .get();
    }
}
