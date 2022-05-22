package com.bloodxtears.serverstatuscollector.controllers;

import com.bloodxtears.serverstatuscollector.dao.StatisticsDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class StatisticsController {
    StatisticsDAO statisticsDAO;

    @GetMapping("/statistics")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/api/statistics", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String statistics(@RequestParam String from, @RequestParam String to) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(statisticsDAO.getStatisticsRange(from,to));
    }

    @Autowired
    public void setStatisticsDAO(StatisticsDAO statisticsDAO) {
        this.statisticsDAO = statisticsDAO;
    }
}
