package com.uj.stxtory.controller;

import com.uj.stxtory.service.deal.notify.StockNotifyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CollectController {

  private final StockNotifyService stockNotifyService;

  public CollectController(StockNotifyService stockNotifyService) {
    this.stockNotifyService = stockNotifyService;
  }

  @GetMapping(value = "/collect/stock")
  public String stocksHistoryCollect() {
    stockNotifyService.saveHistory();
    return "success";
  }
}
