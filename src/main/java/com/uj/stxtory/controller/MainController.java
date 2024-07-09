package com.uj.stxtory.controller;

import com.uj.stxtory.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController{
    @Autowired
    MailService mailService;

    @GetMapping(value = "/")
    public String main(Model model){
        model.addAttribute("targets", mailService.getTargets());
        return "/main";
    }
}
