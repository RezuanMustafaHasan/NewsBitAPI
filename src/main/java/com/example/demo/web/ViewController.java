package com.example.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String home() {
        return "redirect:/admin/articles";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
