package com.uj.stxtory.domain.dto.deal;

import lombok.Data;

import java.util.List;

@Data
public class DealSettingsWrapper {
    private List<DealSettingsInfo> settings;
}
