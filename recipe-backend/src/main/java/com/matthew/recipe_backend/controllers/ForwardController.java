package com.matthew.recipe_backend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardController {

    @RequestMapping(value = {
            "/{path:^(?!api|swagger-ui|v3|actuator|assets|uploads|.*\\.).*$}",
            "/{path:^(?!api|swagger-ui|v3|actuator|assets|uploads|.*\\.).*$}/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
