package com.abcms031.sso.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/welcome")
    public String print(){
        return "welcome";
    }
}
