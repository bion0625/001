package com.uj.stxtory.service.scheduler;

import com.uj.stxtory.domain.entity.Stock;
import com.uj.stxtory.service.mail.MailService;
import com.uj.stxtory.service.stock.TreeDayPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Transactional
@Service
public class SchedulerService {
    @Autowired
    TreeDayPriceService treeDayPriceService;

    @Autowired
    MailService mailService;

    List<Stock> all;

    @PostConstruct
    public void getAllStock() {
        all  = treeDayPriceService.getAll();
    }

    // 월-금: 오후 5시5분마다
    @Scheduled(cron = "0 5 17 ? * MON-FRI")
    public void treeDaysMailSend() {
        all = treeDayPriceService.saveNewByToday(all);
        mailService.treeDaysMailSend(all, " - 선택 종목");
    }

    // 월-금 아침 8시 - 오후 4시: 정각 및 30분마다
    @Scheduled(cron = "0 0/30 8-16 ? * MON-FRI")
    public void treeDaysDataBaseUpdate() {
        if (all.size() == 0) return;
        all = treeDayPriceService.renewalUpdateByToday(all);
    }

    // 월-금 아침 8시 - 오후 4시: 5분 및 35분마다
    @Scheduled(cron = "0 5/35 8-16 ? * MON-FRI")
    public void treeDaysDeletedsMailSend() {
        List<Stock> deleted  = treeDayPriceService.getDeleteByDate();
        if (deleted.size() == 0) return;
        mailService.treeDaysMailSend(deleted, " - 매도 종목");
    }
}
