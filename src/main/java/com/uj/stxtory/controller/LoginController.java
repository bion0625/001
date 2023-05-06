package com.uj.stxtory.controller;

import com.uj.stxtory.domain.dto.LoginUser;
import com.uj.stxtory.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    LoginService loginService;

    @GetMapping(value = "/logout")
    public String logout(HttpSession session){
        session.setAttribute("id",null);
        return "redirect:/";
    }

    @GetMapping(value = "/")
    public String loginPage(Model model, HttpSession session){
        Object idSession = session.getAttribute("id");
        if(idSession != null){
            LoginUser loginUser = loginService.loginSession((String) idSession);
            model.addAttribute("loginUser", loginUser);

            if (loginUser != null && !loginUser.getLoginCheck().isEmpty() && "SUCCESS".equals(loginUser.getLoginCheck())){
                session.setAttribute("id",loginUser.getId());
//            return "main";
            }
        }
        return "login";
    }

    @PostMapping(value = "/")
    public String login(Model model, HttpSession session, String userId, String userPassword){
        LoginUser loginUser = loginService.login(userId, userPassword);
        model.addAttribute("loginUser", loginUser);

        if (loginUser != null && !loginUser.getLoginCheck().isEmpty() && "SUCCESS".equals(loginUser.getLoginCheck())){
            session.setAttribute("id",loginUser.getId());
//            return "main";
        }
        return "login";
    }
}
