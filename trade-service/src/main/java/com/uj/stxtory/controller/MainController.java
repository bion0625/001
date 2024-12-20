package com.uj.stxtory.controller;

import com.uj.stxtory.domain.entity.TbUPbitKey;
import com.uj.stxtory.repository.TbUPbitKeyRepository;
import com.uj.stxtory.service.deal.notify.StockNotifyService;
import com.uj.stxtory.service.deal.notify.UPbitNotifyService;
import com.uj.stxtory.service.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class MainController{
    @Autowired
    MailService mailService;
    @Autowired
    StockNotifyService stockNotifyService;
    @Autowired
    UPbitNotifyService uPbitNotifyService;
    @Autowired
    TbUPbitKeyRepository tbUPbitKeyRepository;

    @GetMapping(value = "/")
    public String mainPage(@AuthenticationPrincipal String loginId){
        if (tbUPbitKeyRepository.findByUserLoginId(loginId)
                .filter(TbUPbitKey::getAutoOn)
                .isPresent()) return "redirect:/select/upbit";
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
