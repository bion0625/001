package com.uj.stxtory.controller;

import com.uj.stxtory.domain.entity.Stock;
import com.uj.stxtory.service.mail.MailService;
import com.uj.stxtory.service.stock.TreeDayPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class StockController {

    @Autowired
    MailService mailService;

    @Autowired
    TreeDayPriceService treeDayPriceService;

    @ResponseBody
    @GetMapping("/stock")
    public boolean stock() {
        List<Stock> all = treeDayPriceService.getAll();
        return mailService.treeDaysMailSend(all);
    }
}
