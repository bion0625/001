package com.uj.stxtory.controller;

import com.uj.stxtory.service.deal.StockService;
import com.uj.stxtory.service.deal.UPbitService;
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
    StockService stockService;
    @Autowired
    UPbitService uPbitService;

    @GetMapping(value = "/")
    public String main(Model model){
        model.addAttribute("targets", mailService.getTargets());
        return "main";
    }

    @GetMapping(value = "/select/stock")
    public String stocks(Model model){
        model.addAttribute("items", stockService.getSaved());
        model.addAttribute("subject", "STOCK SELECT");
        return "main";
    }

    @GetMapping(value = "/select/upbit")
    public String upbits(Model model){
        model.addAttribute("items", uPbitService.getSaved());
        model.addAttribute("subject", "UPBIT SELECT");
        return "main";
    }
}
