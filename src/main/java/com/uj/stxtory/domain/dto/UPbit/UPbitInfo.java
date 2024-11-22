package com.uj.stxtory.domain.dto.UPbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.util.FormatUtil;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class UPbitInfo implements DealItem {

    // todo del
    public static void main(String[] args) {
        List<UPbitPriceInfo> priceInfo = UPbitInfo.getPriceInfo("BTC-NMR", 130);
        System.out.println(priceInfo);
    }

    private String market;
    private String koreanName;
    private String englishName;

    private String name; // 종목명
    private String code; // 종목코드

    private List<UPbitPriceInfo> prices; // 가격정보 리스트

    private double originMinimumSellingPrice;
    private double originExpectedSellingPrice;
    private double minimumSellingPrice;
    private double expectedSellingPrice;
    private LocalDateTime pricingReferenceDate;
    private int renewalCnt;

    @Override
    public void sellingPriceUpdate(Date pricingDate) {
        // 하한 매도 가격은 반올림
        this.minimumSellingPrice = Math.round(this.expectedSellingPrice * 0.95);
        // 기대 매도 가격은 올림
        this.expectedSellingPrice = (long) Math.ceil(this.expectedSellingPrice * 1.1);
        this.renewalCnt++;
        this.pricingReferenceDate = pricingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    //TODO
//    public Stock toEntity() {
//        double high = prices.get(0).getHigh();
//        double minimum = Math.round(high * 0.95);
//        double expected = Math.round(high * 1.1);
//        return new Stock(code, name, minimum, expected, minimum, expected);
//    }

    public static List<UPbitInfo> getAll() {
        String url = "https://api.upbit.com/v1/market/all";

        JsonNode jsonNode = getJsonNodeByUrl(url);

        if (jsonNode.get("error") != null) return Collections.emptyList();

        List<UPbitInfo> coins = new ArrayList<>();
        for (JsonNode node : jsonNode) {
            UPbitInfo coin = new UPbitInfo();
            coin.setCode(node.get("market").asText());
            coin.setName(node.get("korean_name").asText());

            coin.setMarket(node.get("market").asText());
            coin.setKoreanName(node.get("korean_name").asText());
            coin.setEnglishName(node.get("english_name").asText());
            coins.add(coin);
        }

        return coins;
    }

    public static List<UPbitPriceInfo> getPriceInfo(String market, int days) {

        String url = String.format("https://api.upbit.com/v1/candles/days?count=%d&market=%s", days, market);

        JsonNode jsonNode = getJsonNodeByUrl(url);

        if (jsonNode.get("error") != null) return Collections.emptyList();

        FormatUtil formatUtil = new FormatUtil();
        List<UPbitPriceInfo> prices = new ArrayList<>();
        for (JsonNode node : jsonNode) {
            UPbitPriceInfo price = new UPbitPriceInfo();
            price.setDate(formatUtil.stringToDate(node.get("candle_date_time_kst").asText().substring(0, 10).replaceAll("-",".")));
            price.setClose(Double.parseDouble(node.get("trade_price").asText()));
            price.setOpen(Double.parseDouble(node.get("opening_price").asText()));
            price.setHigh(Double.parseDouble(node.get("high_price").asText()));
            price.setLow(Double.parseDouble(node.get("low_price").asText()));
            price.setDiff(Double.parseDouble(node.get("change_price").asText()));
            price.setVolume(Double.parseDouble(node.get("candle_acc_trade_volume").asText()));
            prices.add(price);
        }

        return prices;
    }

    private static JsonNode getJsonNodeByUrl(String url) {
        Document doc = null;
        JsonNode jsonNode = null;
        try {
            doc = getDocumentByUrl(url);
            String jsonData = doc.select("body").text();
            ObjectMapper mapper = new ObjectMapper();
            jsonNode = mapper.readTree(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonNode;
    }

    private static Document getDocumentByUrl(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .ignoreContentType(true)
                .timeout(10000)
                .get();
    }
}
