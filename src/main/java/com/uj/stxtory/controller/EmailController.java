package com.uj.stxtory.controller;

import com.uj.stxtory.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EmailController {
    @Autowired
    MailService mailService;

    @ResponseBody
    @PostMapping("/gmail/target")
    public boolean gmailToken(@RequestBody String target) {
        mailService.gmailTaretEmailSave(target.replaceAll("\"",""));
        return true;
    }
}
