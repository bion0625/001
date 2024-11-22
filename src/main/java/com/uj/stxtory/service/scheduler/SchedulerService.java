package com.uj.stxtory.service.scheduler;

import com.uj.stxtory.service.deal.StockService;
import com.uj.stxtory.service.mail.MailService;
import com.uj.stxtory.service.stock.TreeDayPriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                            mailService.noticeDelete(deleted);
                            return "stock update complete";
                        }).thenAccept(log::info));
    }
}
