package com.example.kromannreumert.casee.controller;

import com.example.kromannreumert.casee.dto.CaseRequestDTO;
import com.example.kromannreumert.casee.dto.CaseResponseDTO;
import com.example.kromannreumert.casee.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cases")
public class CaseController {

    @Autowired
    CaseService caseService;

    @PostMapping("")
    public ResponseEntity<?> createCase(@RequestBody CaseRequestDTO request) {
        try {
            return new ResponseEntity<>(caseService.createCase(request), HttpStatus.OK);
        } catch(RuntimeException e) {
            return new ResponseEntity<>("Failed to create case: " + request.name(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getCases() {
        try {
            return new ResponseEntity<>(caseService.getAllCases(), HttpStatus.OK);
        } catch(RuntimeException e) {
            return new ResponseEntity<>("Failed to retrieve cases", HttpStatus.BAD_REQUEST);
        }
    }



}
