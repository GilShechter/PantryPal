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

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login - PantryPal");
        model.addAttribute("subpage", "login");
        return "index";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Sign Up - PantryPal");
        model.addAttribute("subpage", "signup");
        return "index";
    }

    @GetMapping("/history")
    public String history(Model model) {
        model.addAttribute("title", "History - PantryPal");
        model.addAttribute("subpage", "history");
        return "index";
    }

    @GetMapping("/liked")
    public String liked(Model model) {
        model.addAttribute("title", "Liked Recipes - PantryPal");
        model.addAttribute("subpage", "history");
        return "index";
    }
}
