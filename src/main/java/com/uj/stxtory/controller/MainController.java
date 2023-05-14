package com.uj.stxtory.controller;

import com.uj.stxtory.MsgConstants;
import com.uj.stxtory.domain.dto.LoginUser;
import com.uj.stxtory.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class MainController extends BaseController{
    @GetMapping(value = "/")
    public String main(Model model, HttpServletRequest request){
        return "/main";
    }
}
