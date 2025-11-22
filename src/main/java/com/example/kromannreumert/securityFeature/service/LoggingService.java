package com.example.kromannreumert.securityFeature.service;

import com.example.kromannreumert.securityFeature.entity.LogAction;
import com.example.kromannreumert.securityFeature.entity.Logging;
import com.example.kromannreumert.securityFeature.repository.LogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoggingService {

    private final LogRepository logRepository;

    public LoggingService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void log(LogAction action, String actor, String details) {
        Logging entry = new Logging(actor, action, details);
        logRepository.save(entry);
    }

    public List<Logging> getAllLogs() {
        return logRepository.findAll();
    }
}

