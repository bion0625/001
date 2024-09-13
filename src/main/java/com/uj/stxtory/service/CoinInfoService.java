package com.uj.stxtory.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// https://apidocs.bithumb.com/reference/%EC%9D%BCday-%EC%BA%94%EB%93%A4
public class CoinInfoService {
    public static void main(String[] args) {
        CoinInfoService coinInfoService = new CoinInfoService();
        Set<String> names = coinInfoService.getNameInfo();

        Map<String, List<Map<String, String>>> priceInfoByDay = new HashMap<>();
        names.forEach(name -> priceInfoByDay.put(name, coinInfoService.getPriceInfoByDay(name)));

        System.out.println("priceInfoByDay: " + priceInfoByDay);
    }

    public List<Map<String, String>> getPriceInfoByDay(String name) {
        String url = "https://api.bithumb.com/v1/candles/days";

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        JsonNode jsonNode = getJsonNodeByUrl(url,
                "market", name,
                "to", now.format(formatter),
                "count", "200", // 200일치 가져오기
                "convertingPriceUnit", "KRW"
        );

        if (jsonNode.get("error") != null) return new ArrayList<>();
        List<Map<String, String>> details = new ArrayList<>();
        for (JsonNode node : jsonNode) {
            Map<String, String> detail = new HashMap<>();
            detail.put("candle_date_time_kst", node.get("candle_date_time_kst").asText());
            detail.put("high_price", node.get("high_price").asText());
            detail.put("low_price", node.get("low_price").asText());
            detail.put("trade_price", node.get("trade_price").asText());
            details.add(detail);
        }
        return details;
//        for (JsonNode node : jsonNode) {
//            System.out.println("마켓명: " + node.get("market"));
//            System.out.println("캔들 기준 시각(UTC 기준): " + node.get("candle_date_time_utc"));
//            System.out.println("캔들 기준 시각(KST 기준): " + node.get("candle_date_time_kst"));
//            System.out.println("시가: " + node.get("opening_price"));
//            System.out.println("고가: " + node.get("high_price"));
//            System.out.println("저가: " + node.get("low_price"));
//            System.out.println("종가: " + node.get("trade_price"));
//            System.out.println("캔들 종료 시각(KST 기준): " + node.get("timestamp"));
//            System.out.println("누적 거래 금액: " + node.get("candle_acc_trade_price"));
//            System.out.println("누적 거래량: " + node.get("candle_acc_trade_volume"));
//            System.out.println("전일 종가(UTC 0시 기준): " + node.get("prev_closing_price"));
//            System.out.println("전일 종가 대비 변화 금액: " + node.get("change_price"));
//            System.out.println("전일 종가 대비 변화량: " + node.get("change_rate"));
//            System.out.println("종가 환산 화폐 단위로 환산된 가격(요청에 convertingPriceUnit 파라미터 없을 시 해당 필드 포함되지 않음.): " + node.get("converted_trade_price"));
//            System.out.println();
//        }
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
