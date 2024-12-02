package com.uj.stxtory.controller;

import com.uj.stxtory.domain.entity.TbUser;
import com.uj.stxtory.service.UserService;
import com.uj.stxtory.service.account.UPbitAccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@SessionAttributes("user")
public class UserController {

    private UserService userService;
    private UPbitAccountService uPbitAccountService;
    
    public UserController(UserService userService, UPbitAccountService uPbitAccountService) {
		this.userService = userService;
		this.uPbitAccountService = uPbitAccountService;
	}

    @GetMapping(value = "/login")
    public String loginPage(){
        return "user/login";
    }

    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("user", new TbUser());
        return "user/join";
    }

    @PostMapping("/join")
    public String join(@ModelAttribute("user")TbUser user, BindingResult result, SessionStatus status) {
        boolean idDupl = userService.isIdDupl(user.getUserLoginId());
        if (result.hasErrors() || idDupl) {
            // 아이디 중복 에러 설정
            result.rejectValue("userLoginId", "duplicate.userForm.userLoginId", "이미 사용 중인 아이디입니다.");
            return "user/join";
        }
        userService.save(user);
        status.setComplete();
        return "user/login";
    }
    
    @GetMapping("/my")
    public String my(Model model, Authentication authentication) {
    	String userLoginId = authentication.getPrincipal().toString();

    	var upbitAccounts = uPbitAccountService.getAccount(userLoginId);
    	model.addAttribute("upbitAccounts", upbitAccounts);

    	return "user/my";
    }
}
