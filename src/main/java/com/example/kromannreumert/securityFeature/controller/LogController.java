package com.example.kromannreumert.securityFeature.controller;

import com.example.kromannreumert.securityFeature.entity.Logging;
import com.example.kromannreumert.securityFeature.service.LoggingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class LogController {

    private final LoggingService loggingService;

    public LogController(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    //TODO add Logs for specific actions

    @GetMapping("/getalllogs")
    public List<Logging> getAllLogs() {
        return loggingService.getAllLogs();
    }
}
