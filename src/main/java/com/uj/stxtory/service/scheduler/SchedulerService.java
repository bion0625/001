package com.uj.stxtory.service.scheduler;

import com.uj.stxtory.domain.dto.stock.StockInfo;
import com.uj.stxtory.service.mail.MailService;
import com.uj.stxtory.service.stock.TreeDayPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SchedulerService {
    @Autowired
    TreeDayPriceService treeDayPriceService;

    @Autowired
    MailService mailService;

    @Scheduled(cron = "0 0 17 ? * MON-FRI")
    public void treeDaysMailSend() {
        List<StockInfo> stockInfos = treeDayPriceService.start();
        StringBuilder msg = new StringBuilder();
        for (StockInfo info : stockInfos) {
            String content = String.format("%s\t%s\n", info.getCode(), info.getName());
            System.out.print(content);
            msg.append(content);
        }
        mailService.sendGmail(new Date().getTime() + " - 종목", msg.toString());
    }
}
