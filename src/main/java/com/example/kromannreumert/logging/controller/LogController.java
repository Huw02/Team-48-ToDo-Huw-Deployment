package com.example.kromannreumert.logging.controller;

import com.example.kromannreumert.logging.entity.Logging;
import com.example.kromannreumert.logging.service.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class LogController {

    private static final Logger log = LoggerFactory.getLogger(LogController.class);
    private final LoggingService loggingService;

    public LogController(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    //TODO add Logs for specific actions - NO CRUDS only read.

    // @PreAuthorize("hasRole('ADMIN')") <-- add preAuthorize when we are ready
    @GetMapping("/getalllogs")
    public List<Logging> getAllLogs(Principal principal) {
        log.info("Controller: Get all logs has been accessed by {}", principal.getName());
        return loggingService.getAllLogs(principal.getName());
    }

    @GetMapping("getlog/{logId}")
    public Logging getOneLog(@PathVariable int logId, Principal principal){
        return loggingService.getLogById(logId, principal.getName());
    }
}
