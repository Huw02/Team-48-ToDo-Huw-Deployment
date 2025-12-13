package com.example.kromannreumert.logging.service;

import com.example.kromannreumert.logging.repository.LogRepository;
import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.entity.Logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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


    public List<Logging> getAllLogs(String name) {
        try{
            List<Logging>logs = logRepository.findAll();

            log(LogAction.VIEW_ALL_LOGS, name, "Viewed all logs");

            return logs;

        }catch (RuntimeException e){

            log(LogAction.VIEW_ALL_LOGS_FAILED, name, "Failed to view logs");

            throw new RuntimeException("could not view all logs");
        }

    }

    public List<Logging> getAllLogsByAction(LogAction action) {
        return logRepository.findAllByAction(action);
    }

    public Logging getLogById(int id, String name) {
        try {
            Optional<Logging>logs = logRepository.findById(id);

            log(LogAction.VIEW_ONE_LOG, name, "viewed one log: " + logs.get());

            return logs.get();
        } catch (RuntimeException e) {
            log(LogAction.VIEW_ONE_LOG_FAILED, name, "Failed to view one log");
            throw new RuntimeException("could not view log");
        }
    }
}

