package site.kimnow.toy.menu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/menu")
public class MenuController {

    @GetMapping
    public String index(Model model) {
        model.addAttribute("apiKey", "");
        return "menu/index";
    }
}
