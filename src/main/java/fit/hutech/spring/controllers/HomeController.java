package fit.hutech.spring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public String home() {
        return "home/index";
    }
    @GetMapping("/api/list")
    public String apiList() {
        return "api/list";
    }
}
