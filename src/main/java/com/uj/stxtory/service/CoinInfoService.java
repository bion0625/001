package com.uj.stxtory.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uj.stxtory.domain.dto.CoinInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// https://apidocs.bithumb.com/reference/%EC%9D%BCday-%EC%BA%94%EB%93%A4
public class CoinInfoService {
    public static void main(String[] args) {
        CoinInfoService coinInfoService = new CoinInfoService();
        Set<String> names = coinInfoService.getNameInfo();
//        System.out.println("names: " + names);
//
//        List<CoinInfo> priceInfoByDay = coinInfoService.getPriceInfoByDay("KRW-MEV", 3);
//        System.out.println("priceInfoByDay: " + priceInfoByDay);
//
//        // 130일 신고가
//        OptionalDouble max = coinInfoService.getPriceInfoByDay("KRW-MEV", 130).stream().mapToDouble(c -> Double.parseDouble(c.getHigh_price())).max();
//        System.out.println("max: " + max);

        // 오늘의 고가 저장
//        Map<String, List<CoinInfo>> todaies = names.stream()
//                .map(name -> coinInfoService.getPriceInfoByDay(name, 1).get(0))
//                .collect(Collectors.groupingBy(CoinInfo::getMarket));

        List<CoinInfo> coins = names.stream()
                // 오늘 정보
                .map(name -> coinInfoService.getPriceInfoByDay(name, 1).get(0))
                .filter(info -> {
                    List<CoinInfo> priceInfoByDay = coinInfoService.getPriceInfoByDay(info.getMarket(), 60);
                    // 신고가 여부 확인
                    OptionalDouble high = priceInfoByDay.stream()
                            .mapToDouble(c -> Double.parseDouble(c.getHigh_price()))
                            .max();
                    if (high.isEmpty()) return false;
                    if (high.getAsDouble() != Double.parseDouble(info.getHigh_price())) return false;

                    // 3연달아 상승여부 확인
                    List<CoinInfo> threeUp = priceInfoByDay.stream().limit(3).collect(Collectors.toList());

                    return IntStream.range(1, threeUp.size())
                            .allMatch(upIdx -> Double.parseDouble(threeUp.get(upIdx).getHigh_price()) < Double.parseDouble(threeUp.get(upIdx-1).getHigh_price()))
                        && IntStream.range(1, threeUp.size())
                            .allMatch(downIdx -> Double.parseDouble(threeUp.get(downIdx).getLow_price()) < Double.parseDouble(threeUp.get(downIdx-1).getLow_price()));
                })
                .collect(Collectors.toList());
        System.out.println("coins: ");
        for (CoinInfo coinInfo : coins) {
            System.out.println(coinInfo);
        }


//        Map<String, List<CoinInfo>> priceInfoByDay = new HashMap<>();
//        names.forEach(name -> priceInfoByDay.put(name, coinInfoService.getPriceInfoByDay(name)));
//
//        System.out.println("priceInfoByDay: " + priceInfoByDay);
    }

    public List<CoinInfo> getPriceInfoByDay(String name, int day) {
        String url = "https://api.bithumb.com/v1/candles/days";

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        JsonNode jsonNode = getJsonNodeByUrl(url,
                "market", name,
                "to", now.format(formatter),
                "count", String.valueOf(day), // day일치 가져오기
                "convertingPriceUnit", "KRW"
        );

        if (jsonNode.get("error") != null) return new ArrayList<>();

        List<CoinInfo> coins = new ArrayList<>();
        for (JsonNode node : jsonNode) {
            CoinInfo coinInfo = new CoinInfo(
                    node.get("market").asText(),
                    node.get("candle_date_time_utc").asText(),
                    node.get("candle_date_time_kst").asText(),
                    node.get("opening_price").asText(),
                    node.get("high_price").asText(),
                    node.get("low_price").asText(),
                    node.get("trade_price").asText(),
                    node.get("timestamp").asText(),
                    node.get("candle_acc_trade_price").asText(),
                    node.get("candle_acc_trade_volume").asText(),
                    node.get("prev_closing_price").asText(),
                    node.get("change_price").asText(),
                    node.get("change_rate").asText(),
                    node.get("converted_trade_price").asText()
            );
            coins.add(coinInfo);
        }

        return coins;
    }

    public Set<String> getNameInfo() {
        JsonNode jsonNode = getJsonNodeByUrl("https://api.bithumb.com/v1/market/all");
        Set<String> names = new HashSet<>();
        for (JsonNode node : jsonNode) names.add(node.get("market").asText());
        return names;
    }
    
    private JsonNode getJsonNodeByUrl(String url) {
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

    private JsonNode getJsonNodeByUrl(String url,
                                      String param1Name, String param1Value, String param2Name, String param2Value,
                                      String param3Name, String param3Value, String param4Name, String param4Value) {
        Document doc = null;
        JsonNode jsonNode = null;
        try {
            doc = getDocumentByUrl(url,
                    param1Name, param1Value, param2Name, param2Value,
                    param3Name, param3Value, param4Name, param4Value);
            String jsonData = doc.select("body").text();
            ObjectMapper mapper = new ObjectMapper();
            jsonNode = mapper.readTree(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonNode;
    }

    private Document getDocumentByUrl(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .ignoreContentType(true)
                .timeout(10000)
                .get();
    }

    private Document getDocumentByUrl(String url,
                                      String param1Name, String param1Value, String param2Name, String param2Value,
                                      String param3Name, String param3Value, String param4Name, String param4Value
    ) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .ignoreContentType(true)
                .timeout(10000)
                .data(param1Name, param1Value)
                .data(param2Name, param2Value)
                .data(param3Name, param3Value)
                .data(param4Name, param4Value)
                .get();
    }
}
