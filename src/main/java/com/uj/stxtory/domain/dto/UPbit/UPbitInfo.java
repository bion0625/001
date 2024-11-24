package com.uj.stxtory.domain.dto.upbit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.dto.deal.DealPrice;
import com.uj.stxtory.domain.entity.UPbit;
import com.uj.stxtory.util.ApiDelayUtil;
import com.uj.stxtory.util.FormatUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UPbitInfo implements DealItem {

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
    private double tempPrice;
    private LocalDateTime pricingReferenceDate;
    private int renewalCnt;

    @Override
    public void setPrices(List<DealPrice> prices) {
        this.prices = prices.stream().map(p -> (UPbitPriceInfo) p).collect(Collectors.toList());
    }

    @Override
    public void sellingPriceUpdate(Date pricingDate) {
        // 하한 매도 가격은 반올림
        this.minimumSellingPrice = Math.round(this.expectedSellingPrice * 0.95);
        // 기대 매도 가격은 올림
        this.expectedSellingPrice = (long) Math.ceil(this.expectedSellingPrice * 1.1);
        this.renewalCnt++;
        this.pricingReferenceDate = pricingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public UPbitInfo(String name, String code,
                     double originMinimumSellingPrice,
                     double originExpectedSellingPrice,
                     double minimumSellingPrice,
                     double expectedSellingPrice,
                     double tempPrice,
                     LocalDateTime pricingReferenceDate,
                     int renewalCnt)  {
        this.name = name;
        this.code = code;
        this.originMinimumSellingPrice = originMinimumSellingPrice;
        this.originExpectedSellingPrice = originExpectedSellingPrice;
        this.minimumSellingPrice = minimumSellingPrice;
        this.expectedSellingPrice = expectedSellingPrice;
        this.pricingReferenceDate = pricingReferenceDate;
        this.tempPrice = tempPrice;
        this.renewalCnt = renewalCnt;
    }

    @Override
    public UPbit toEntity() {
        double high = prices.get(0).getHigh();
        double temp = prices.get(0).getClose();
        double minimum = Math.round(high * 0.95);
        double expected = Math.round(high * 1.1);
        return new UPbit(code, name, minimum, expected, minimum, expected, temp);
    }

    public static UPbitInfo fromEntity(UPbit uPbit) {
        return new UPbitInfo(
                uPbit.getName(),
                uPbit.getCode(),
                uPbit.getOriginMinimumSellingPrice(),
                uPbit.getOriginExpectedSellingPrice(),
                uPbit.getMinimumSellingPrice(),
                uPbit.getExpectedSellingPrice(),
                uPbit.getTempPrice(),
                uPbit.getPricingReferenceDate(),
                uPbit.getRenewalCnt());
    }

    public static List<DealItem> getAll() {
        String url = "https://api.upbit.com/v1/market/all";

        JsonNode jsonNode = getJsonNodeByUrl(url);

        if (jsonNode.get("error") != null) return Collections.emptyList();

        List<DealItem> coins = new ArrayList<>();
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

    public static List<DealPrice> getPriceInfoByDay(String market, int days) {

        String url = String.format("https://api.upbit.com/v1/candles/days?count=%d&market=%s", days, market);

        JsonNode jsonNode = getJsonNodeByUrl(url);

        if (jsonNode.get("error") != null) return Collections.emptyList();

        List<DealPrice> prices = new ArrayList<>();

        for (JsonNode node : jsonNode) {
            UPbitPriceInfo price = new UPbitPriceInfo();
            price.setDate(FormatUtil.stringToDate(node.get("candle_date_time_kst").asText().substring(0, 10).replaceAll("-",".")));
            price.setClose(FormatUtil.StringToDouble(node.get("trade_price").asText()));
            price.setOpen(FormatUtil.StringToDouble(node.get("opening_price").asText()));
            price.setHigh(FormatUtil.StringToDouble(node.get("high_price").asText()));
            price.setLow(FormatUtil.StringToDouble(node.get("low_price").asText()));
            price.setDiff(FormatUtil.StringToDouble(node.get("change_price").asText()));
            price.setVolume(FormatUtil.StringToDouble(node.get("candle_acc_trade_volume").asText()));
            prices.add(price);
        }

        return prices;
    }

    public static Optional<UPbitPriceInfo> getPriceInfoByToday(String market) {

        String url = String.format("https://api.upbit.com/v1/ticker?markets=%s", market);

        JsonNode jsonNode = getJsonNodeByUrl(url);

        if (jsonNode.get("error") != null) return Optional.empty();

        return Optional.ofNullable(jsonNode.get(0)).map(node -> {
            UPbitPriceInfo price = new UPbitPriceInfo();
            price.setDate(new Date());
            price.setClose(FormatUtil.StringToDouble(node.get("trade_price").asText()));
            price.setOpen(FormatUtil.StringToDouble(node.get("opening_price").asText()));
            price.setHigh(FormatUtil.StringToDouble(node.get("high_price").asText()));
            price.setLow(FormatUtil.StringToDouble(node.get("low_price").asText()));
            price.setDiff(FormatUtil.StringToDouble(node.get("change_price").asText()));
            price.setVolume(FormatUtil.StringToDouble(node.get("acc_trade_volume").asText()));
            return price;
        });
    }

    private static JsonNode getJsonNodeByUrl(String url) {
        Document doc;
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
        // 업비트 요청 수 제한으로 시간 제한 [초] 걸기
        ApiDelayUtil.setDelay(3);

        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .ignoreContentType(true)
                .timeout(10000)
                .get();
    }
}
