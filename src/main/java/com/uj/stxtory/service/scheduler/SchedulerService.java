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

    @Scheduled(cron = "0 0 17 ? * MON-FRI")
    public void treeDaysMailSend() {
        mailService.treeDaysMailSend();
    }

    @Scheduled(cron = "0 10 17 ? * MON-FRI")
    public void treeDaysDataBaseUpdate() {
        all = treeDayPriceService.saveNewByToday(all);
        all = treeDayPriceService.renewalUpdateByToday(all);
    }
}
