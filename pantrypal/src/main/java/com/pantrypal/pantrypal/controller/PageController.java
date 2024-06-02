package com.pantrypal.pantrypal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "PantryPal - Home");
        model.addAttribute("subpage", "home");
        return "index";
    }

    @GetMapping("/upload")
    public String upload(Model model) {
        model.addAttribute("title", "Upload Image - PantryPal");
        model.addAttribute("subpage", "upload");
        return "index";
    }
}
