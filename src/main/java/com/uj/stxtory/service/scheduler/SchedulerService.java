package com.uj.stxtory.service.scheduler;

import com.uj.stxtory.service.deal.StockService;
import com.uj.stxtory.service.deal.UPbitService;
import com.uj.stxtory.service.mail.MailService;
import com.uj.stxtory.service.stock.TreeDayPriceService;
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
public class SchedulerService {
    @Autowired
    TreeDayPriceService treeDayPriceService;

    @Autowired
    MailService mailService;


    @Autowired
    StockService stockService;
    @Autowired
    UPbitService uPbitService;

    // 월-금 아침 8시 - 오후 4시: 정각 및 20분, 40분 마다
    @Scheduled(cron = "0 0/15 8-16 ? * MON-FRI")
    public void treeDaysSave() {
//        CompletableFuture.supplyAsync(() -> {
//            treeDayPriceService.saveNewByToday();
//            return "treeDaysSave complete";
//        }).thenAccept(log::info);

        CompletableFuture.supplyAsync(() -> {
            stockService.save();
            return "stock save complete";
        }).thenAccept(log::info);
    }

    // 월-금 아침 8시 - 오후 4시: 정각 마다
    @Scheduled(cron = "0 0 8-16 ? * MON-FRI")
    public void treeDaysMailSend() {
        CompletableFuture.supplyAsync(() -> treeDayPriceService.getAll())
                .thenCompose(all -> CompletableFuture.supplyAsync(
                        () -> mailService.treeDaysMailSend(all, " - 선택 종목"))
                        .thenAccept(result -> log.info("treeDaysMailSend Complete")));
    }

    // 월-금 아침 8시 - 오후 4시: 정각 및 15분, 30분, 45분 마다
    @Scheduled(cron = "0 0/15 8-16 ? * MON-FRI")
    public void treeDaysDataBaseUpdate() {
//        CompletableFuture.supplyAsync(() -> {
//            treeDayPriceService.renewalUpdateByToday();
//            return "treeDaysDataBaseUpdate complete";
//        }).thenAccept(log::info);

        CompletableFuture.supplyAsync(() -> stockService.update().getDeleteItems())
                .thenApplyAsync(deleted ->
                        CompletableFuture.supplyAsync(() -> {
                            mailService.noticeDelete(deleted, "stock");
                            return "stock update complete";
                        }).thenAccept(log::info));
    }

    // UPbit!

    // 매일 1시간마다
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    public void uPbitSave() {
        CompletableFuture.supplyAsync(() -> {
            uPbitService.save();
            return "UPbit save complete";
        }).thenAccept(log::info);
    }

    // 매일 정각마다
    @Scheduled(cron = "0 0 * ? * *")
    public void uPbitMailSend() {
        CompletableFuture.supplyAsync(() -> uPbitService.getSaved())
                .thenCompose(all -> CompletableFuture.supplyAsync(
                        () -> {
                            mailService.noticeSelect(new ArrayList<>(all), "upbit");
                            return "UPbit main send Complete";
                        })
                        .thenAccept(log::info));
    }

    // 매일 5분마다
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void uPbitUpdate() {
        CompletableFuture.supplyAsync(() -> uPbitService.update().getDeleteItems())
                .thenApplyAsync(deleted ->
                        CompletableFuture.supplyAsync(() -> {
                            mailService.noticeDelete(deleted, "upbit");
                            return "upbit update complete";
                        }).thenAccept(log::info));
    }
}
