package com.example.kromannreumert.casee.service;

import com.example.kromannreumert.casee.dto.CaseRequestDTO;
import com.example.kromannreumert.casee.dto.CaseResponseDTO;
import com.example.kromannreumert.casee.entity.Casee;
import com.example.kromannreumert.casee.repository.CaseRepository;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;

    public CaseService(CaseRepository caseRepository, UserRepository userRepository) {
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
    }

    public List<Casee> getAllCases() {
        return caseRepository.findAll();
    }


    public Casee getCaseByName(String caseName) {
        return caseRepository.findAllByName(caseName).orElseThrow(() -> new RuntimeException("Case not found"));
    }


    public CaseResponseDTO createCase(CaseRequestDTO request) {

        try {
            Set<User> users = request.users().stream()
                    .map(username -> userRepository.findByUsername(username.getUsername())
                            .orElseThrow(() -> new RuntimeException("User not found: " + username)))
                    .collect(Collectors.toSet());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        caseRepository.save(new Casee(request.name(), request.client(), request.users(), request.idPrefix()));

        return new CaseResponseDTO(request.name(), request.client(), request.users(), request.idPrefix());
    }


    private Casee updateCase() {
        return null; //TODO
    }

    public String deleteCase(Long id) {
        caseRepository.deleteById(id);
        return "Case with id: " + id + " has been deleted";
    }
}
