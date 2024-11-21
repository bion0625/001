package com.uj.stxtory.domain.dto.deal;

import java.util.Date;

public interface DealPrice {
    long getVolume();
    long getDiff();
    long getClose();
    long getHigh();
    long getLow();
    Date getDate();
}
