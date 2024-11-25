package com.uj.stxtory.service.deal.scheduler;

import com.uj.stxtory.service.deal.DealSchedulerService;
import com.uj.stxtory.service.deal.notify.StockNotifyService;
import com.uj.stxtory.service.deal.notify.UPbitNotifyService;
import com.uj.stxtory.service.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Transactional
@Service
public class StockSchedulerService implements DealSchedulerService {
    @Autowired
    MailService mailService;
    @Autowired
    StockNotifyService stockNotifyService;
    @Autowired
    UPbitNotifyService uPbitNotifyService;

    // 월-금 아침 8시 - 오후 4시: 정각 및 20분, 40분 마다
    @Override
    @Scheduled(cron = "0 0/15 8-16 ? * MON-FRI")
    public void save() {
        CompletableFuture.supplyAsync(() -> {
            stockNotifyService.save();
            return "\n\n\nstock save complete";
        }).thenAccept(log::info);
    }

    // 월-금 아침 8시 - 오후 4시: 정각 및 15분, 30분, 45분 마다
    @Override
    @Scheduled(cron = "0 0/15 8-16 ? * MON-FRI")
    public void update() {
        mailService.noticeDelete(stockNotifyService.update().getDeleteItems(), "STOCK");
        log.info("\n\n\nstock update & mail send complete");
    }

    // 월-금 아침 8시 - 오후 4시: 정각 마다
    @Override
    @Scheduled(cron = "0 0 8-16 ? * MON-FRI")
    public void mail() {
        mailService.noticeSelect(new ArrayList<>(stockNotifyService.getSaved()), "STOCK");
        log.info("\n\n\nSTOCK mail send Complete");
    }
}
