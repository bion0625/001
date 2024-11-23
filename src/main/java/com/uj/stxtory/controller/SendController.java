package com.uj.stxtory.controller;

import com.uj.stxtory.domain.dto.UPbit.UPbitInfo;
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
import java.util.stream.Collectors;

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
        List<StockInfo> all = stockService.getSaved().stream().map(StockInfo::fromEntity).collect(Collectors.toList());
        mailService.noticeSelect(new ArrayList<>(all), " - 선택 종목");
        return true;
    }

    @ResponseBody
    @GetMapping("/upbit")
    public boolean upbit() {
        List<UPbitInfo> all = uPbitService.getSaved().stream().map(UPbitInfo::fromEntity).collect(Collectors.toList());
        mailService.noticeSelect(new ArrayList<>(all), " - 선택 종목");
        return true;
    }
}
