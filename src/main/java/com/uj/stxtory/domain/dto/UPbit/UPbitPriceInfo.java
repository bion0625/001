package com.uj.stxtory.domain.dto.UPbit;

import com.uj.stxtory.domain.dto.deal.DealPrice;
import lombok.Data;

import java.util.Date;

@Data
public class UPbitPriceInfo implements DealPrice {
    private Date date;
    private double close;
    private double diff;
    private double open;
    private double high;
    private double low;
    private double volume;
}
