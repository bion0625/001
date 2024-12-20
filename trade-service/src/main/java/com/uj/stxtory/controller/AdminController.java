package com.uj.stxtory.controller;

import com.uj.stxtory.domain.dto.UserListDto;
import com.uj.stxtory.service.UserService;
import com.uj.stxtory.service.mail.MailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {
    private final MailService mailService;
    private final UserService userService;

    public AdminController(MailService mailService, UserService userService) {
        this.mailService = mailService;
        this.userService = userService;
    }

    @GetMapping(value = "/admin")
    public String admin(Model model){
        model.addAttribute("targets", mailService.getTargets());
        return "admin/admin";
    }

    @GetMapping(value = "/admin/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAllForAdmin());
        return "admin/users";
    }

    @PostMapping("/admin/users")
    public String updateUsers(@ModelAttribute UserListDto userListDto) {
        userService.setRoleAndDel(userListDto);
        return "redirect:/admin/users";
    }

    @PostMapping(value = "/actuator/adminLogin")
    @ResponseBody
    public String adminTest(@RequestBody String loginId){
        return userService.getAdmin(loginId);
    }
}
