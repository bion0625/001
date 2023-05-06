package com.uj.stxtory.controller;

import com.uj.stxtory.MsgConstants;
import com.uj.stxtory.domain.dto.LoginUser;
import com.uj.stxtory.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;

@Controller
public class BaseController {

    @Autowired
    LoginService loginService;

    public String sessionCheck(Model model, HttpSession session){
        Object idSession = session.getAttribute("id");
        if(idSession == null){
            return "redirect:/";
        }
        LoginUser loginUser = loginService.loginSession((String) idSession);
        model.addAttribute("loginUser", loginUser);
        if(loginUser == null || loginUser.getLoginCheck().isEmpty() || !"SUCCESS".equals(loginUser.getLoginCheck())){
            return "redirect:/";
        }
        session.setAttribute("id",loginUser.getId());
        return MsgConstants.SUCCESS;
    }
}
