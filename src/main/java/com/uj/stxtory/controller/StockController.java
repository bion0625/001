package com.uj.stxtory.controller;

import com.uj.stxtory.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StockController {

    @Autowired
    MailService mailService;

    @ResponseBody
    @GetMapping("/stock")
    public boolean stock() {
        return mailService.treeDaysMailSend();
    }
}
