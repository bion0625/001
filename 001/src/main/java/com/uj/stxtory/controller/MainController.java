package com.uj.stxtory.controller;

import com.uj.stxtory.service.deal.notify.StockNotifyService;
import com.uj.stxtory.service.deal.notify.UPbitNotifyService;
import com.uj.stxtory.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController{
    @Autowired
    MailService mailService;
    @Autowired
    StockNotifyService stockNotifyService;
    @Autowired
    UPbitNotifyService uPbitNotifyService;

    @GetMapping(value = "/")
    public String mainPage(Model model){
        model.addAttribute("targets", mailService.getTargets());
        return "main";
    }

    @GetMapping(value = "/select/stock")
    public String stocks(Model model){
        model.addAttribute("items", stockNotifyService.getSaved());
        model.addAttribute("subject", "STOCK SELECT");
        return "main";
    }

    @GetMapping(value = "/select/upbit")
    public String upbits(Model model){
        model.addAttribute("items", uPbitNotifyService.getSaved());
        model.addAttribute("subject", "UPBIT SELECT");
        return "main";
    }
}
