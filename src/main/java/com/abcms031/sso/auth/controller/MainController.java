package com.abcms031.sso.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 화면을 리턴해주는 controller
@Controller

// api 쓸때 주로 사용됨
// ResponseBody 가 추가된 친구
//@RestController
public class MainController {

    @GetMapping("/welcome")
    public String print(){
        return "welcome";
    }

    @GetMapping("/sign-in")
    public String signIn(){
        return "sign_in";
    }
}
