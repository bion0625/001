package com.uj.stxtory.domain.dto.stock;

import com.uj.stxtory.domain.dto.deal.DealPrice;
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

    public DealPrice toDealPrice() {
        return new DealPrice(this.date, this.close, this.diff, this.open, this.high, this.low, this.volume);
    }
}
