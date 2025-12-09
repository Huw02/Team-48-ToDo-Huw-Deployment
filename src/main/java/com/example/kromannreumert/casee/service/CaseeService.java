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
import com.example.kromannreumert.exception.customException.http4xxExceptions.ApiBusinessException;
import com.example.kromannreumert.exception.customException.http4xxExceptions.UserNotFoundException;
import com.example.kromannreumert.exception.customException.http4xxExceptions.casee.CaseNotFoundException;
import com.example.kromannreumert.exception.customException.http4xxExceptions.client.ClientNotFoundException;
import com.example.kromannreumert.exception.customException.http5xxException.ActionFailedException;
import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CaseeService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final CaseMapper caseMapper;
    private final LoggingService loggingService;
    private final ClientRepository clientRepository;

    public CaseeService(CaseRepository caseRepository,
                        UserRepository userRepository,
                        CaseMapper caseMapper,
                        LoggingService loggingService,
                        ClientRepository clientRepository) {
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
        this.caseMapper = caseMapper;
        this.loggingService = loggingService;
        this.clientRepository = clientRepository;
    }

    public List<Casee> getAllCases(Principal principal) {
        try {
            User currentUser = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new UserNotFoundException(
                            LogAction.VIEW_ONE_USER_FAILED,
                            principal.getName(),
                            "username='" + principal.getName() + "'"
                    ));

            boolean isJurist = currentUser.getRoles().stream()
                    .anyMatch(r -> r.getRoleName().equals("JURIST"));

            if (isJurist) {
                return caseRepository.findDistinctByUsers_UserId(currentUser.getUserId());
            }

            return caseRepository.findAll();
        } catch (Exception e) {
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.CASE_READ, principal.getName(), e);
        }
    }


    public Casee getCaseByName(String caseName) {
        try {
            return caseRepository.findAllByName(caseName)
                    .orElseThrow(() -> new CaseNotFoundException(
                            LogAction.CASE_READ,
                            null,
                            "name='" + caseName + "'"
                    ));
        } catch (Exception e) {
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.CASE_READ, null, e);
        }
    }


    public CaseResponseDTO createCase(CaseRequestDTO request, Principal principal) {

        try {
            Client client = clientRepository.findById(request.clientId())
                    .orElseThrow(() -> new ClientNotFoundException(
                            LogAction.CASE_CREATE,
                            principal.getName(),
                            "id=" + request.clientId()
                    ));

            Set<User> users = request.userIds().stream()
                    .map(id -> userRepository.findById(Math.toIntExact(id))
                            .orElseThrow(() -> new UserNotFoundException(
                                    LogAction.VIEW_ONE_USER_FAILED,
                                    principal.getName(),
                                    "id=" + id
                            )))
                    .collect(Collectors.toSet());

            User responsibleUser = userRepository.findById(request.responsibleUserId())
                    .orElseThrow(() -> new UserNotFoundException(
                            LogAction.VIEW_ONE_USER_FAILED,
                            principal.getName(),
                            "id=" + request.responsibleUserId()
                    ));

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
        } catch (Exception e) {
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.CASE_CREATE, principal.getName(), e);
        }
    }



    public CaseResponseDTO updateCase(CaseUpdateRequest request, Principal principal) {

        try {
            Casee target = caseRepository.findById(request.id())
                    .orElseThrow(() -> new CaseNotFoundException(
                            LogAction.CASE_UPDATE,
                            principal.getName(),
                            "id=" + request.id()
                    ));

            User responsible = userRepository.findById(request.responsibleUserId())
                    .orElseThrow(() -> new UserNotFoundException(
                            LogAction.VIEW_ONE_USER_FAILED,
                            principal.getName(),
                            "id=" + request.responsibleUserId()
                    ));

            Set<User> assignees = request.assigneeIds().stream()
                    .map(id -> userRepository.findById(id)
                            .orElseThrow(() -> new UserNotFoundException(
                                    LogAction.VIEW_ONE_USER_FAILED,
                                    principal.getName(),
                                    "id=" + id
                            )))
                    .collect(Collectors.toSet());

            loggingService.log(LogAction.CASE_UPDATE, principal.getName(), " Updated case: " + target.getName() + "\nID: " + target.getIdPrefix() + "\nAssignees: " + target.getUsers() + "\nTo\n" + request.name() + "\nID: " + request.idPrefix() + "\nAssignees: " + assignees);

            target.setName(request.name());
            target.setIdPrefix(request.idPrefix());
            target.setResponsibleUser(responsible);
            target.setUsers(assignees);



            Casee saved = caseRepository.save(target);
            return caseMapper.caseToResponse(saved);
        } catch (Exception e) {
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.CASE_UPDATE, principal.getName(), e);
        }
    }


    public String deleteCase(CaseDeleteRequestDTO request, Principal principal) {
        try {
            Casee target = caseRepository.findById(request.id())
                    .orElseThrow(() -> new CaseNotFoundException(
                            LogAction.CASE_DELETE,
                            principal.getName(),
                            "id=" + request.id()
                    ));

            loggingService.log(LogAction.CASE_DELETE, principal.getName(), " Deleted case: " + target.getName() + "\nID: " + target.getIdPrefix() + "\nAssignees: " + target.getUsers());
            caseRepository.delete(target);

            return "Case deleted successfully";


        } catch (Exception e) {
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.CASE_DELETE, principal.getName(), e);
        }
    }
}
