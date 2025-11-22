package com.example.kromannreumert.healthz;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StatusController {

    @GetMapping("/status/healthz")
    public Map<String, String> healthzStatus() {
        return Map.of("Status:", "We are live");
    }
}
