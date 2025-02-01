package com.uj.stxtory.service.deal.scheduler;

import com.uj.stxtory.config.DealDaysConfig;
import com.uj.stxtory.service.deal.DealSchedulerService;
import com.uj.stxtory.service.deal.notify.StockNotifyService;
import com.uj.stxtory.service.mail.MailService;
import com.uj.stxtory.util.ApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Slf4j
@Transactional
@Service
public class StockSchedulerService implements DealSchedulerService {
    @Autowired
    private final MailService mailService;
    @Autowired
    private final StockNotifyService stockNotifyService;
    @Autowired
    private final DealDaysConfig dealDaysConfig;

    public StockSchedulerService(MailService mailService, StockNotifyService stockNotifyService, DealDaysConfig dealDaysConfig) {
        this.mailService = mailService;
        this.stockNotifyService = stockNotifyService;
        this.dealDaysConfig = dealDaysConfig;
    }

    // 월-금 아침 8시 - 오후 4시: 정각 및 20분, 40분 마다
    @Override
    @Scheduled(cron = "0 0/15 8-16 ? * MON-FRI")
    public void save() {
        int baseDays = dealDaysConfig.getStockBaseDays();
        stockNotifyService.save();
        log.info("\n\n\nstock save complete("+ baseDays +")\n\n\n");
    }

    // 월-금 아침 8시 - 오후 4시: 정각 및 15분, 30분, 45분 마다
    @Override
    @Scheduled(cron = "0 0/15 8-16 ? * MON-FRI")
    public void update() {
        ApiUtil.runWithException(
                () -> mailService.noticeDelete(stockNotifyService.update().getDeleteItems(), "STOCK"));
        log.info("\n\n\nstock update & mail send complete\n\n\n");
    }

    // 월-금 아침 8시 - 오후 4시: 정각 마다
    @Override
    @Scheduled(cron = "0 0 8-16 ? * MON-FRI")
    public void mail() {
        ApiUtil.runWithException(
                () -> mailService.noticeSelect(new ArrayList<>(stockNotifyService.getSaved()), "STOCK"));
        log.info("\n\n\nSTOCK mail send Complete\n\n\n");
    }
}
