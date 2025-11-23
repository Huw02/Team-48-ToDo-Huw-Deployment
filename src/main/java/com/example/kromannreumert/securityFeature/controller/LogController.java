package com.example.kromannreumert.securityFeature.controller;

import com.example.kromannreumert.securityFeature.entity.Logging;
import com.example.kromannreumert.securityFeature.service.LoggingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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

    //TODO add Logs for specific actions - NO CRUDS only read.

    // @PreAuthorize("hasRole('ADMIN')") <-- add preAuthorize when we are ready
    @GetMapping("/getalllogs")
    public List<Logging> getAllLogs() {
        return loggingService.getAllLogs();
    }
}
