package com.example.kromannreumert.casee.service;

import com.example.kromannreumert.casee.dto.CaseDeleteRequestDTO;
import com.example.kromannreumert.casee.dto.CaseRequestDTO;
import com.example.kromannreumert.casee.dto.CaseResponseDTO;
import com.example.kromannreumert.casee.dto.CaseUpdateRequest;
import com.example.kromannreumert.casee.entity.Casee;
import com.example.kromannreumert.casee.mapper.CaseMapper;
import com.example.kromannreumert.casee.repository.CaseRepository;
import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.client.repository.ClientRepository;
import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final CaseMapper caseMapper;
    private final LoggingService loggingService;
    private final ClientRepository clientRepository;

    public CaseService(CaseRepository caseRepository, UserRepository userRepository, CaseMapper caseMapper, LoggingService loggingService, ClientRepository clientRepository) {
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
        this.caseMapper = caseMapper;
        this.loggingService = loggingService;
        this.clientRepository = clientRepository;
    }

    public List<Casee> getAllCases(Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If user is a JURIST, only return cases assigned to them
        boolean isJurist = currentUser.getRoles().stream()
                .anyMatch(r -> r.getRoleName().equals("JURIST"));

        if (isJurist) {
            return caseRepository.findDistinctByUsers_UserId(currentUser.getUserId());
        }

        // All other roles see all cases
        return caseRepository.findAll();
    }



    public Casee getCaseByName(String caseName) {
        return caseRepository.findAllByName(caseName).orElseThrow(() -> new RuntimeException("Case not found"));
    }


    public CaseResponseDTO createCase(CaseRequestDTO request, Principal principal) {

        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new RuntimeException("Client not found: " + request.clientId()));

        Set<User> users = request.userIds().stream()
                .map(id -> userRepository.findById(Math.toIntExact(id))
                        .orElseThrow(() -> new RuntimeException("User not found: " + id)))
                .collect(Collectors.toSet());

        User responsibleUser = userRepository.findById(request.responsibleUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Casee newCase = new Casee(
                request.name(),
                client,
                users,
                request.idPrefix(),
                responsibleUser
        );

        caseRepository.save(newCase);

        loggingService.log(
                LogAction.CASE_CREATE,
                principal.getName(),
                principal.getName() + " created case: " + request.name() + "\nID: " + request.idPrefix() + "\nResposible User: " + responsibleUser + "\nAssignees: " + users
        );

        return new CaseResponseDTO(
                newCase.getName(),
                newCase.getClient(),
                newCase.getUsers(),
                newCase.getIdPrefix(),
                newCase.getResponsibleUser()
        );
    }



    public CaseResponseDTO updateCase(CaseUpdateRequest request, Principal principal) {

        Casee target = caseRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Case not found"));

        User responsible = userRepository.findById(request.responsibleUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Set<User> assignees = request.assigneeIds().stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("User not found")))
                .collect(Collectors.toSet());

        loggingService.log(LogAction.CASE_UPDATE, principal.getName(), " Updated case: " + target.getName() + "\nID: " + target.getIdPrefix() + "\nAssignees: " + target.getUsers() + "\nTo\n" + request.name() + "\nID: " + request.idPrefix() + "\nAssignees: " + assignees);

        target.setName(request.name());
        target.setIdPrefix(request.idPrefix());
        target.setResponsibleUser(responsible);
        target.setUsers(assignees);



        Casee saved = caseRepository.save(target);
        return caseMapper.caseToResponse(saved);
    }


    public String deleteCase(CaseDeleteRequestDTO request, Principal principal) {
        Casee target = caseRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Case not found"));

        loggingService.log(LogAction.CASE_DELETE, principal.getName(), " Deleted case: " + target.getName() + "\nID: " + target.getIdPrefix() + "\nAssignees: " + target.getUsers());
        caseRepository.delete(target);

        return "Case deleted successfully";


    }
}
