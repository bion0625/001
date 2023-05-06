package com.uj.stxtory.controller;

import com.uj.stxtory.MsgConstants;
import com.uj.stxtory.service.JoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class JoinController {

    @Autowired
    JoinService joinService;

    @GetMapping(value = "/join")
    public String joinPage(){
        return "join";
    }

    @PostMapping(value = "/join")
    @ResponseBody
    public Map<String, String> join(Model model, @RequestBody Map<String, String> data){
        String userId = data.get("userId");
        String userName = data.get("userName");
        String userPassword = data.get("userPassword");
        String userEmail = data.get("userEmail");
        String userPhone = data.get("userPhone");
        if(userId.trim().isEmpty() || userName.trim().isEmpty() || userPassword.trim().isEmpty()){
            data.put("MSG",MsgConstants.JOIN_ACCOUNT_EX_001);
            return data;
        }
        String joinComplete = joinService.join(userId, userName, userPassword, userEmail, userPhone);
        if (MsgConstants.SUCCESS.equals(joinComplete)){
            data.put("MSG",MsgConstants.SUCCESS);
            return data;
        }else if(MsgConstants.DUPLICATE_ID.equals(joinComplete)){
            data.put("MSG",userId + MsgConstants.JOIN_ID_EX_001);
            return data;
        }
        data.put("MSG",userId + MsgConstants.JOIN_BASE_EX_001);
        return data;
    }

}
