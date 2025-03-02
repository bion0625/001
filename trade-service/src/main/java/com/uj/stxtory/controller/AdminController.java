package com.uj.stxtory.controller;

import com.uj.stxtory.domain.dto.AutoMangerDto;
import com.uj.stxtory.domain.dto.AutoMangerListDto;
import com.uj.stxtory.domain.dto.UserListDto;
import com.uj.stxtory.domain.dto.deal.DealSettingsInfo;
import com.uj.stxtory.domain.dto.deal.DealSettingsWrapper;
import com.uj.stxtory.domain.entity.TbUPbitKey;
import com.uj.stxtory.service.DealSettingsService;
import com.uj.stxtory.service.UserService;
import com.uj.stxtory.service.account.upbit.UPbitAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class AdminController {
    private final UserService userService;
    private final UPbitAccountService uPbitAccountService;
    private final DealSettingsService dealSettingsService;

    public AdminController(UserService userService, UPbitAccountService uPbitAccountService, DealSettingsService dealSettingsService) {
        this.userService = userService;
        this.uPbitAccountService = uPbitAccountService;
        this.dealSettingsService = dealSettingsService;
    }

    // 메일링 종료
//    @GetMapping(value = "/admin")
//    public String admin(Model model){
//        model.addAttribute("targets", mailService.getTargets());
//        return "admin/mail";
//    }

    @GetMapping(value = "/admin/setting")
    public String settingPage(Model model) {

        List<DealSettingsInfo> updatedSettings = new ArrayList<>();

        updatedSettings.add(dealSettingsService.getByName("upbit"));
        updatedSettings.add(dealSettingsService.getByName("stock"));

        model.addAttribute("settings", updatedSettings);

        return "admin/setting";
    }

    @PostMapping(value = "/admin/setting")
    public String setting(@ModelAttribute DealSettingsWrapper wrapper) {
        if (Optional.of(wrapper).map(DealSettingsWrapper::getSettings).isPresent())
            wrapper.getSettings().forEach(dealSettingsService::update);
        return "redirect:/admin/setting";
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
