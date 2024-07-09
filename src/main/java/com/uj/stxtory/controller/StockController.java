package com.uj.stxtory.controller;

import com.uj.stxtory.domain.dto.stock.StockInfo;
import com.uj.stxtory.service.mail.MailService;
import com.uj.stxtory.service.stock.TreeDayPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
public class StockController {

    @Autowired
    TreeDayPriceService treeDayPriceService;

    @Autowired
    MailService mailService;

    @ResponseBody
    @GetMapping("/stock")
    public List<StockInfo> stock() {
        List<StockInfo> stockInfos = treeDayPriceService.start();
        StringBuilder msg = new StringBuilder();
        for (StockInfo info : stockInfos) {
            String content = String.format("%s\t%s\n", info.getCode(), info.getName());
            System.out.print(content);
            msg.append(content);
        }
        mailService.sendGmail(new Date().getTime() + " - 종목", msg.toString());
        return stockInfos;
    }
}
