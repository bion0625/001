package com.uj.stxtory.service.deal.scheduler;

import com.uj.stxtory.service.deal.DealSchedulerService;
import com.uj.stxtory.service.deal.notify.USStockNotifyService;
import com.uj.stxtory.util.ApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
public class USStockSchedulerService implements DealSchedulerService {
    @Autowired
    USStockNotifyService usStockNotifyService;

    // 매일 3시간 마다
    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 3)
    public void save() {
    	usStockNotifyService.save();
    	log.info("\n\n\nUSStock save complete\n\n\n");
    }

    // 1분마다
    @Override
    @Scheduled(cron = "0 0/30 22-23 * 3-11 *") // 22:30~23:59 (당일)
    @Scheduled(cron = "0 0/30 0-5 * 11-12,1-2 *") // 00:00~05:59 (익일)
    public void update() {
        ApiUtil.runWithException(
                () -> usStockNotifyService.update());
        log.info("\n\n\nUSStock update\n\n\n");
    }

    @Override
    public void mail() {
    }
}
