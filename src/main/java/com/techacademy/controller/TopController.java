package com.techacademy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TopController {

    public TopController() {

    }

    @GetMapping(value = "/login")
    public String login() {
        return "login/login";
    }
}
