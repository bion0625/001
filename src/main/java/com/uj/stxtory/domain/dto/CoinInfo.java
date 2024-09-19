package com.uj.stxtory.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinInfo {
    // 마켓명
    private String market;
    // 캔들 기준 시각(UTC 기준)
    private String candle_date_time_utc;
    // 캔들 기준 시각(KST 기준)
    private String candle_date_time_kst;
    // 시가
    private String opening_price;
    // 고가
    private String high_price;
    // 저가
    private String low_price;
    // 종가
    private String trade_price;
    // 캔들 종료 시각(KST 기준)
    private String timestamp;
    // 누적 거래 금액
    private String candle_acc_trade_price;
    // 누적 거래량
    private String candle_acc_trade_volume;
    // 전일 종가(UTC 0시 기준)
    private String prev_closing_price;
    // 전일 종가 대비 변화 금액
    private String change_price;
    // 전일 종가 대비 변화량
    private String change_rate;
    // 종가 환산 화폐 단위로 환산된 가격
    private String converted_trade_price;
}
