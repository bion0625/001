package com.uj.stxtory.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // 에러 처리 로직을 추가 예정.
        return "/util/error"; // 에러 페이지 템플릿 이름 또는 경로
    }
}
