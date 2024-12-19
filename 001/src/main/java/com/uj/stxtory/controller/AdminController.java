package com.uj.stxtory.controller;

import com.uj.stxtory.domain.entity.TbUser;
import com.uj.stxtory.repository.UserRepository;
import com.uj.stxtory.service.mail.MailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminController {
    private final MailService mailService;
    private final UserRepository userRepository;

    public AdminController(MailService mailService, UserRepository userRepository) {
        this.mailService = mailService;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/admin")
    public String admin(Model model){
        model.addAttribute("targets", mailService.getTargets());
        return "admin";
    }

    @PostMapping(value = "/actuator/adminLogin")
    @ResponseBody
    public String adminTest(@RequestBody String loginId){
        return userRepository.findByUserLoginId(loginId)
                .filter(u -> u.getUserRole().equals("ADMIN"))
                .map(TbUser::getUserPassword).orElseThrow();
    }
}
