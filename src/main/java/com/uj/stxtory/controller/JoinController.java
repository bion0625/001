package com.uj.stxtory.controller;

import com.uj.stxtory.MsgConstants;
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
        if (MsgConstants.SUCCESS.equals(joinComplete)){
            return "redirect:/";
        }else if(MsgConstants.DUPLICATE_ID.equals(joinComplete)){
            model.addAttribute(MsgConstants.DUPLICATE_ID,userId + MsgConstants.JOIN_ID_EX_001);
        }
        return "join";
    }

}
