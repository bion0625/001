package com.uj.stxtory.domain.dto.stock;

import lombok.Data;

import java.util.Date;

@Data
public class StockPriceInfo {
    private Date date;
    private long close;
    private long diff;
    private long open;
    private long high;
    private long low;
    private long volume;
}
