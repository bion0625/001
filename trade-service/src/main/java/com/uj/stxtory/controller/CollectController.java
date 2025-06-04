package com.uj.stxtory.controller;

import com.uj.stxtory.service.deal.notify.StockNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CollectController {

  private final StockNotifyService stockNotifyService;

  public CollectController(StockNotifyService stockNotifyService) {
    this.stockNotifyService = stockNotifyService;
  }

  @GetMapping(value = "/collect/stock")
  public String stocksHistoryCollect() {
    log.info("\n\n\nstock saveHistory start\n\n\n");
    stockNotifyService.saveHistory();
    log.info("\n\n\nstock saveHistory complete\n\n\n");
    return "success";
  }
}
