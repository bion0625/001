package com.uj.stxtory.controller;

import com.uj.stxtory.domain.dto.AutoMangerDto;
import com.uj.stxtory.domain.dto.AutoMangerListDto;
import com.uj.stxtory.domain.dto.UserListDto;
import com.uj.stxtory.domain.entity.TbUPbitKey;
import com.uj.stxtory.service.UserService;
import com.uj.stxtory.service.account.upbit.UPbitAccountService;
import com.uj.stxtory.service.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class AdminController {
    private final MailService mailService;
    private final UserService userService;
    private final UPbitAccountService uPbitAccountService;

    public AdminController(MailService mailService, UserService userService, UPbitAccountService uPbitAccountService) {
        this.mailService = mailService;
        this.userService = userService;
        this.uPbitAccountService = uPbitAccountService;
    }

    @GetMapping(value = "/admin")
    public String admin(Model model){
        model.addAttribute("targets", mailService.getTargets());
        return "admin/mail";
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

    @GetMapping("/admin/autos")
    public String autos(Model model) {
        List<TbUPbitKey> autoAccount = uPbitAccountService.getAutoAccount();
        List<AutoMangerDto> autos = userService.getAllForAdmin().stream().map(u -> new AutoMangerDto(
                u.getUserLoginId(),
                u.getUserName(),
                autoAccount.stream().anyMatch(key -> key.getUserLoginId().equals(u.getUserLoginId())) ? "ON" : "OFF")).collect(Collectors.toList());

        model.addAttribute("autos", autos);
        return "admin/autos";
    }

    @PostMapping("/admin/autos")
    public String autos(@ModelAttribute AutoMangerListDto autoMangerListDto) {
        autoMangerListDto.getAutoMangers()
                .forEach(dto ->
                        uPbitAccountService.updateAuto(dto.getUserLoginId(), "ON".equals(dto.getUpbitAuto())));
        return "redirect:/admin/autos";
    }

    @PostMapping(value = "/actuator/adminLogin")
    @ResponseBody
    public String adminTest(@RequestBody String loginId){
        return userService.getAdmin(loginId);
    }
}
