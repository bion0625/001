package com.uj.stxtory.domain.dto.upbit;

import com.uj.stxtory.domain.dto.deal.DealPrice;
import java.util.Date;
import lombok.Data;

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
