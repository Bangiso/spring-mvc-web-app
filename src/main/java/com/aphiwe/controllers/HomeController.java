package com.aphiwe.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("privacy")
    private String privacy() {
        return "privacy";
    }
    @GetMapping
    private String index() {
        return "index";
    }
}
