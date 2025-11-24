package com.example.kromannreumert.healthz;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StatusController {

    private static final Logger log = LoggerFactory.getLogger(StatusController.class);

    @GetMapping("/status/healthz")
    public Map<String, String> healthzStatus() {
        log.info("Health status endpoint has been accesed");
        return Map.of("Status:", "We are live");
    }
}
