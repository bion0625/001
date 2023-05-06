package com.uj.stxtory.controller;

import com.uj.stxtory.service.JoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class JoinController {

    @Autowired
    JoinService joinService;

    @GetMapping(value = "/join")
    public String joinPage(){
        return "join";
    }

    @PostMapping(value = "/join")
    public String join(Model model,
                       String userId, String userName, String userPassword,
                       String userEmail, String userPhone){
        String joinComplete = joinService.join(userId, userName, userPassword, userEmail, userPhone);
        if ("SUCCESS".equals(joinComplete)){
            return "redirect:/";
        }else if("duplicateId".equals(joinComplete)){
            model.addAttribute("duplicateId",userId + "는 이미 존재하는 아이디입니다.");
        }
        return "join";
    }

}
