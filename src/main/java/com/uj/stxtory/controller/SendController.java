package com.uj.stxtory.controller;

import com.uj.stxtory.domain.dto.upbit.UPbitInfo;
import com.uj.stxtory.domain.dto.stock.StockInfo;
import com.uj.stxtory.service.deal.StockService;
import com.uj.stxtory.service.deal.UPbitService;
import com.uj.stxtory.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SendController {

    @Autowired
    MailService mailService;
    @Autowired
    StockService stockService;
    @Autowired
    UPbitService uPbitService;

    @ResponseBody
    @GetMapping("/stock")
    public boolean stock() {
        List<StockInfo> all = stockService.getSaved();
        mailService.noticeSelect(new ArrayList<>(all), "STOCK");
        return true;
    }

    @ResponseBody
    @GetMapping("/upbit")
    public boolean upbit() {
        List<UPbitInfo> all = uPbitService.getSaved();
        mailService.noticeSelect(new ArrayList<>(all), "UPbit");
        return true;
    }
}
