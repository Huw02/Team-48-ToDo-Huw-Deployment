package com.example.kromannreumert.casee.controller;

import com.example.kromannreumert.casee.dto.CaseDeleteRequestDTO;
import com.example.kromannreumert.casee.dto.CaseRequestDTO;
import com.example.kromannreumert.casee.dto.CaseResponseDTO;
import com.example.kromannreumert.casee.dto.CaseUpdateRequest;
import com.example.kromannreumert.casee.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/cases")
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
        try {
            return new ResponseEntity<>(caseService.getAllCases(principal), HttpStatus.OK);
        } catch(RuntimeException e) {
            return new ResponseEntity<>("Failed to retrieve cases", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("")
    public ResponseEntity<?> updateCase(@RequestBody CaseUpdateRequest request, Principal principal) {
        try {
            return new ResponseEntity<>(caseService.updateCase(request, principal), HttpStatus.OK);
        } catch(RuntimeException e) {
            return new ResponseEntity<>("Failed to update case" + request.name(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteCase(@RequestBody CaseDeleteRequestDTO request, Principal principal) {
        try {
            return ResponseEntity.ok(caseService.deleteCase(request, principal));
        } catch(RuntimeException e) {
            return new ResponseEntity<>("Failed to delete case" + request.id(), HttpStatus.BAD_REQUEST);
        }
    }


    }




