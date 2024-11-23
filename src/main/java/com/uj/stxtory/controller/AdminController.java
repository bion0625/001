package com.uj.stxtory.controller;

import com.uj.stxtory.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    @Autowired
    MailService mailService;

    @GetMapping(value = "/admin")
    public String admin(Model model){
        model.addAttribute("targets", mailService.getTargets());
        return "/admin";
    }

    @GetMapping(value = "/admin/test")
    public String adminTest(Model model){
        model.addAttribute("targets", mailService.getTargets());
        return "/main";
    }
}
