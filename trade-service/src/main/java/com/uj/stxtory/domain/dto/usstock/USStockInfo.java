package com.uj.stxtory.domain.dto.usstock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.dto.deal.DealPrice;
import com.uj.stxtory.domain.entity.USStock;
import com.uj.stxtory.util.ApiUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 미국 주식 상위 500개
 * */
@Data
@Slf4j
@NoArgsConstructor
public class USStockInfo implements DealItem {

    private String name; // 종목명
    private String code; // 종목코드 (Ticker)
    private List<USStockPriceInfo> prices; // 가격 정보 리스트
    private double originMinimumSellingPrice;
    private double originExpectedSellingPrice;
    private double minimumSellingPrice;
    private double expectedSellingPrice;
    private double tempPrice;
    private LocalDateTime updatedAt;
    private LocalDateTime pricingReferenceDate;
    private int renewalCnt;
    public USStockInfo(String name, String code, double tempPrice) {
        this.name = name;
        this.code = code;
        this.tempPrice = tempPrice;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public void setPrices(List<DealPrice> prices) {
        this.prices = prices.stream().map(p -> (USStockPriceInfo) p).collect(Collectors.toList());
    }

    @Override
    public void sellingPriceUpdate(Date pricingDate) {
        this.minimumSellingPrice = Math.round(this.tempPrice * 0.95);
        this.expectedSellingPrice = Math.ceil(this.tempPrice * 1.1);
        this.renewalCnt++;
        this.pricingReferenceDate = LocalDateTime.now();
    }

    public USStock toEntity() {
        return new USStock(
                this.code,
                this.name,
                this.minimumSellingPrice,
                this.expectedSellingPrice,
                this.minimumSellingPrice,
                this.expectedSellingPrice,
                this.tempPrice
        );
    }

    public static USStockInfo fromEntity(USStock usStock) {
        return new USStockInfo(
                usStock.getName(),
                usStock.getCode(),
                usStock.getTempPrice()
        );
    }

    // 상위 500개만 가져오기
    public static List<DealItem> getCompanyInfo() {
        List<USStockInfo> allStocks = new ArrayList<>();

        // S&P 500 외 다른 데이터를 포함하려면 URL 리스트를 추가
        List<String> urls = List.of(
                "https://www.slickcharts.com/sp500",
                "https://www.slickcharts.com/dowjones", // 다우 존스
                "https://www.slickcharts.com/nasdaq100" // 나스닥 100
        );

        for (String url : urls) {
            try {
                // Jsoup으로 웹 페이지 가져오기
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36")
                        .timeout(15000)
                        .get();

                // 테이블에서 각 행 데이터를 가져오기
                Elements rows = doc.select("table tbody tr");
                for (Element row : rows) {
                    String code = row.select("td:nth-child(3)").text().trim(); // 세 번째 열: 종목 코드
                    String name = row.select("td:nth-child(2)").text().trim(); // 두 번째 열: 종목명

                    // 유효한 데이터만 처리
                    if (!code.isEmpty() && !name.isEmpty()) {
                        USStockInfo stock = new USStockInfo();
                        stock.setName(name);
                        stock.setCode(code);
                        allStocks.add(stock);
                    }
                }
            } catch (IOException e) {
                log.info("Error fetching company info from URL: " + url);
                e.printStackTrace();
            }
        }

        return new ArrayList<>(allStocks);
    }

    // 현재 주가의 종가(실시간 가격)만 가져옴
    public static Optional<Double> getClose(String code) throws IOException {
        String url = String.format("https://finance.yahoo.com/quote/%s", code);
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get();

        // 종목명 가져오기
//        Element nameElement = doc.selectFirst("h1"); // 종목명은 일반적으로 <h1> 태그에 있음

        // 현재 주가 가져오기
        Element priceElement = doc.selectFirst("fin-streamer[data-field=regularMarketPrice]");
        if (priceElement == null) {
            log.warn("Failed to fetch price for code: " + code);
            return Optional.empty();
        }

        return Optional.of(priceElement)
                .map(Element::text)
                .map(t -> t.replace(",", ""))
                .map(Double::parseDouble);
    }

    public static List<DealPrice> getPriceInfoByPage(String code, int day, String API_KEY) {
        final String BASE_URL = "https://api.polygon.io/v2/aggs/ticker";

        List<DealPrice> prices = new ArrayList<>();
        OkHttpClient httpClient = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        // 현재 날짜와 시작 날짜 계산
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = toDate.minusDays(day);

        String url = String.format("%s/%s/range/1/day/%s/%s?apiKey=%s",
                BASE_URL, code, fromDate, toDate, API_KEY);

        try {
            Request request = new Request.Builder().url(url).build();
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("HTTP request failed: " + response.code());
                }

                String jsonData = response.body().string();
                JsonNode rootNode = objectMapper.readTree(jsonData);

                JsonNode results = rootNode.get("results");
                if (results != null) {
                    results.forEach(data -> {
                        USStockPriceInfo price = new USStockPriceInfo();
                        price.setDate(new java.sql.Date(data.get("t").asLong()));
                        price.setOpen(data.get("o").asDouble());
                        price.setHigh(data.get("h").asDouble());
                        price.setLow(data.get("l").asDouble());
                        price.setClose(data.get("c").asDouble());
                        price.setVolume(data.get("v").asLong());
                        prices.add(price);
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching data for ticker: " + code);
            e.printStackTrace();
        }

        // 요청 수 제한으로 시간 제한 [초] 걸기
        ApiUtil.setDelay(15);

        return new ArrayList<>(prices);
    }
}
