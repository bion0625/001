package com.uj.stxtory.service.deal.scheduler;

import com.uj.stxtory.service.deal.DealSchedulerService;
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
public class UPbitSchedulerService implements DealSchedulerService {
    @Autowired
    MailService mailService;
    @Autowired
    UPbitNotifyService uPbitNotifyService;

    // 매일 1시간마다
    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    public void save() {
        CompletableFuture.supplyAsync(() -> {
            uPbitNotifyService.save();
            return "\n\n\nUPbit save complete";
        }).thenAccept(log::info);
    }

    // 매일 5분마다
    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void update() {
        CompletableFuture.supplyAsync(() -> uPbitNotifyService.update().getDeleteItems())
                .thenApplyAsync(deleted ->
                        CompletableFuture.supplyAsync(() -> {
                            mailService.noticeDelete(deleted, "UPbit");
                            return "\n\n\nupbit update & mail send complete";
                        }).thenAccept(log::info));
    }

    // 매일 정각마다
    @Override
    @Scheduled(cron = "0 0 * ? * *")
    public void mail() {
        CompletableFuture.supplyAsync(() -> uPbitNotifyService.getSaved())
                .thenCompose(all -> CompletableFuture.supplyAsync(
                        () -> {
                            mailService.noticeSelect(new ArrayList<>(all), "UPbit");
                            return "\n\n\nUPbit mail send Complete";
                        })
                        .thenAccept(log::info));
    }
}