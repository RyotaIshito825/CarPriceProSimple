package com.techacademy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//@RestController
@Controller
public class TestController {

    @GetMapping(value = "tests")
    public String test() {

        return "pricecards/pricecard1";
    }
}