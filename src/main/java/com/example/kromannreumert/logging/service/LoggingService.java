package com.example.kromannreumert.logging.service;

import com.example.kromannreumert.logging.repository.LogRepository;
import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.entity.Logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoggingService {

    private final static Logger log = LoggerFactory.getLogger(LoggingService.class);
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

    public List<Logging> getAllLogsByAction(LogAction action) {
        return logRepository.findAllByAction(action);
    }
}

