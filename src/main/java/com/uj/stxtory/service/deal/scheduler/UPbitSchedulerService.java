package com.uj.stxtory.service.deal.scheduler;

import com.uj.stxtory.service.deal.DealSchedulerService;
import com.uj.stxtory.service.deal.notify.UPbitNotifyService;
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
public class UPbitSchedulerService implements DealSchedulerService {
    @Autowired
    MailService mailService;
    @Autowired
    UPbitNotifyService uPbitNotifyService;

    // 매일 1시간마다
    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    public void save() {
    	uPbitNotifyService.save();
    	log.info("\nUPbit save complete\n\n\n");
    }

    // 매일 5분마다
    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void update() {
        ApiUtil.runWithException(
                () -> mailService.noticeDelete(uPbitNotifyService.update().getDeleteItems(), "UPbit"));
        log.info("\nstock update & mail send complete\n\n\n");
    }

    // 매일 정각마다
    @Override
    @Scheduled(cron = "0 0 * ? * *")
    public void mail() {
        ApiUtil.runWithException(
                () -> mailService.noticeSelect(new ArrayList<>(uPbitNotifyService.getSaved()), "UPbit"));
        log.info("\nSTOCK mail send Complete\n\n\n");
    }
}
