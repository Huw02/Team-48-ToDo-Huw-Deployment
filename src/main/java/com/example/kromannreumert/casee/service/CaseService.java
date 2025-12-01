package com.example.kromannreumert.casee.service;

import com.example.kromannreumert.casee.dto.CaseDeleteRequestDTO;
import com.example.kromannreumert.casee.dto.CaseRequestDTO;
import com.example.kromannreumert.casee.dto.CaseResponseDTO;
import com.example.kromannreumert.casee.dto.CaseUpdateRequest;
import com.example.kromannreumert.casee.entity.Casee;
import com.example.kromannreumert.casee.mapper.CaseMapper;
import com.example.kromannreumert.casee.repository.CaseRepository;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final CaseMapper caseMapper;

    public CaseService(CaseRepository caseRepository, UserRepository userRepository, CaseMapper caseMapper) {
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
        this.caseMapper = caseMapper;
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
                    .map(user -> userRepository.findByUsername(user.getUsername())
                            .orElseThrow(() -> new RuntimeException("User not found: " + user)))
                    .collect(Collectors.toSet());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        caseRepository.save(new Casee(request.name(), request.client(), request.users(), request.idPrefix()));

        return new CaseResponseDTO(request.name(), request.client(), request.users(), request.idPrefix());
    }


    public CaseResponseDTO updateCase(CaseUpdateRequest request) {

        Casee target = caseRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Case not found"));

        User responsible = userRepository.findById(request.responsibleUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Set<User> assignees = request.assigneeIds().stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("User not found")))
                .collect(Collectors.toSet());

        target.setName(request.name());
        target.setIdPrefix(request.idPrefix());
        target.setResponsibleUser(responsible);
        target.setUsers(assignees);

        Casee saved = caseRepository.save(target);
        return caseMapper.caseToResponse(saved);
    }


    public String deleteCase(CaseDeleteRequestDTO request) {
        Casee target = caseRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Case not found"));

        caseRepository.delete(target);

        return "Case deleted successfully";


    }
}
