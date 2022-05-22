package com.bloodxtears.serverstatuscollector.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatisticsController {
    @GetMapping("/statistics")
    public String index() {
        return "index";
    }
}
