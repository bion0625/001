package com.uj.stxtory.domain.dto.stock;

import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.entity.DividendStock;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class DividendStockInfo {

    public static List<DividendStock> getDividendStocks() {
        return StockInfo.getCompanyInfo().parallelStream()
                .map(info -> DividendStock.of(info.getCode(), info.getName(), getDividendStock(info.getCode())))
                .filter(Objects::nonNull)
                .filter(ds -> !ds.getDividendRate().equals(0.0))
                .toList();
    }

    // 배당 수익률 가져오기
    public static double getDividendStock(String code) {
        double dividendRate = 0.0;

        String url = String.format("https://finance.naver.com/item/main.naver?code=%s", code);
        Document doc = StockInfo.getDocumentByUrl(url);
        if (doc == null) {
            return dividendRate;
        }

        try {
            // 배당수익률(%) 값을 가져옴
            Elements dividendRateElements = doc.select("em#_dvr");

            for (Element el : dividendRateElements) {
                if (el != null) {
                    String text = el.text().replace("%", "").trim(); // "4.56%" → "4.56"
                    if (!text.isEmpty() && text.matches(".*\\d.*")) {
                        dividendRate = Double.parseDouble(text); // 문자열을 double로 파싱
                    }
                }
            }
        } catch (Exception e) {
            log.warn("checkIfDividendStock failed: " + code, e);
            dividendRate = 0.0;
        }
        return dividendRate;
    }
}
