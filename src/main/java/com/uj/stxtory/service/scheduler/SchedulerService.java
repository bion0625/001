package com.uj.stxtory.service.scheduler;

import com.uj.stxtory.domain.entity.Stock;
import com.uj.stxtory.service.mail.MailService;
import com.uj.stxtory.service.stock.TreeDayPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class SchedulerService {
    @Autowired
    TreeDayPriceService treeDayPriceService;

    @Autowired
    MailService mailService;

    // 월-금 아침 8시 - 오후 4시: 정각 및 20분, 40분 마다
    @Scheduled(cron = "0 0/15 8-16 ? * MON-FRI")
    public void treeDaysSave() {
        treeDayPriceService.saveNewByToday();
    }

    // 월-금 아침 8시 - 오후 4시: 정각 마다
    @Scheduled(cron = "0 0 8-16 ? * MON-FRI")
    public void treeDaysMailSend() {
        List<Stock> all = treeDayPriceService.getAll();
        mailService.treeDaysMailSend(all, " - 선택 종목");
    }

    // 월-금 아침 8시 - 오후 4시: 정각 및 15분, 30분, 45분 마다
    @Scheduled(cron = "0 0/15 8-16 ? * MON-FRI")
    public void treeDaysDataBaseUpdate() {
        treeDayPriceService.renewalUpdateByToday();
    }

}
