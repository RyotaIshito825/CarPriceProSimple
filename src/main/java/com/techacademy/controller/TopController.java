package com.techacademy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TopController {


    @GetMapping(value = "/")
    public String index() {
        return "top";
    }
}
