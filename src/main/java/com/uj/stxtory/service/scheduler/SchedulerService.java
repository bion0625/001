package com.uj.stxtory.service.scheduler;

import com.uj.stxtory.service.mail.MailService;
import com.uj.stxtory.service.stock.TreeDayPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class SchedulerService {
    @Autowired
    TreeDayPriceService treeDayPriceService;

    @Autowired
    MailService mailService;

    @Scheduled(cron = "0 0 17 ? * MON-FRI")
    public void treeDaysMailSend() {
        mailService.treeDaysMailSend();
    }
}
