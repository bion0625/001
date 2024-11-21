package com.uj.stxtory.domain.dto.stock;

import com.uj.stxtory.domain.dto.deal.DealPrice;
import lombok.Data;

import java.util.Date;

@Data
public class StockPriceInfo implements DealPrice {
    private Date date;
    private long close;
    private long diff;
    private long open;
    private long high;
    private long low;
    private long volume;
}
