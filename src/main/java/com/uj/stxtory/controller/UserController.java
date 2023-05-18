package com.uj.stxtory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {

    @GetMapping(value = "/login")
    public String loginPage(Model model, HttpServletRequest request){
        return "/login";
    }
}
