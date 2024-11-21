package com.uj.stxtory.domain.dto.deal;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class DealPrice {
    private Date date;
    private long close;
    private long diff;
    private long open;
    private long high;
    private long low;
    private long volume;
}
