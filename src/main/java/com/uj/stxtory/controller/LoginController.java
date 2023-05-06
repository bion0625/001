package com.uj.stxtory.controller;

import com.uj.stxtory.domain.dto.LoginUser;
import com.uj.stxtory.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    LoginService loginService;

    @GetMapping(value = "/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute("id",null);
        return "redirect:/";
    }

    @GetMapping(value = "/")
    public String loginPage(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        Object idSession = session.getAttribute("id");
        if(idSession != null){
            LoginUser loginUser = loginService.loginSession((String) idSession);
            model.addAttribute("loginUser", loginUser);

            if (loginUser != null && !loginUser.getLoginCheck().isEmpty() && "SUCCESS".equals(loginUser.getLoginCheck())){
                session.setAttribute("id",loginUser.getId());
            return "redirect:/main";
            }
        }
        return "/login";
    }

    @PostMapping(value = "/")
    @ResponseBody
    public LoginUser login(HttpServletRequest request, @RequestBody Map<String, String> data){
        HttpSession session = request.getSession();
        LoginUser loginUser = loginService.login(data.get("userId"), data.get("userPassword"));
        if (loginUser != null && !loginUser.getLoginCheck().isEmpty() && "SUCCESS".equals(loginUser.getLoginCheck())){
            session.setAttribute("id",loginUser.getId());
        }
        return loginUser;
    }
}
