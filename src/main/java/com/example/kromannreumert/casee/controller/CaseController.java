package com.example.kromannreumert.casee.controller;

import com.example.kromannreumert.casee.dto.CaseDeleteRequestDTO;
import com.example.kromannreumert.casee.dto.CaseRequestDTO;
import com.example.kromannreumert.casee.dto.CaseResponseDTO;
import com.example.kromannreumert.casee.dto.CaseUpdateRequest;
import com.example.kromannreumert.casee.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/cases")
@CrossOrigin("*")
public class CaseController {

    @Autowired
    CaseService caseService;

    @PostMapping("")
    public ResponseEntity<?> createCase(@RequestBody CaseRequestDTO request, Principal principal) {
        try {
            CaseResponseDTO response = caseService.createCase(request, principal);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Failed to create case: " + request.name(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("")
    public ResponseEntity<?> getCases(Principal principal) {
        return new ResponseEntity<>(caseService.getAllCases(principal), HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<?> updateCase(@RequestBody CaseUpdateRequest request, Principal principal) {
        return new ResponseEntity<>(caseService.updateCase(request, principal), HttpStatus.OK);
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteCase(@RequestBody CaseDeleteRequestDTO request, Principal principal) {
        return ResponseEntity.ok(caseService.deleteCase(request, principal));
    }


    }




