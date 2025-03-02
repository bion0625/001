package com.uj.stxtory.domain.dto.deal;

import com.uj.stxtory.domain.entity.DealSettings;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DealSettingsInfo {
    private String name;
    private Long expectedHighPercentage;
    private Long expectedLowPercentage;
    private int highestPriceReferenceDays; // 신고가 기준일 수

    public static DealSettingsInfo Basic(String name) {
        return new DealSettingsInfo(name, 10L, 5L, 130);
    }

    public static DealSettingsInfo fromEntity(DealSettings entity) {
        return new DealSettingsInfo(entity.getName(), entity.getExpectedHighPercentage(), entity.getExpectedLowPercentage(), entity.getHighestPriceReferenceDays());
    }
}
