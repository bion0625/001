package com.uj.stxtory.controller;

import com.uj.stxtory.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @Autowired
    LoginService loginService;

    @GetMapping(value = "/")
    public String loginPage(){
        return "login";
    }

    @PostMapping(value = "/login")
    public String login(Model model, String userId, String userPassword){
        model.addAttribute("loginCheck", loginService.login(userId, userPassword));
        return "login";
    }
}
