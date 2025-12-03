package com.example.kromannreumert.unitTest.casee;

import com.example.kromannreumert.casee.dto.CaseDeleteRequestDTO;
import com.example.kromannreumert.casee.dto.CaseRequestDTO;
import com.example.kromannreumert.casee.dto.CaseResponseDTO;
import com.example.kromannreumert.casee.dto.CaseUpdateRequest;
import com.example.kromannreumert.casee.entity.Casee;
import com.example.kromannreumert.casee.mapper.CaseMapper;
import com.example.kromannreumert.casee.repository.CaseRepository;
import com.example.kromannreumert.casee.service.CaseService;
import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.client.repository.ClientRepository;
import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class CaseServiceUnitTest {

    @InjectMocks
    CaseService caseService;

    @Mock
    CaseRepository caseRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ClientRepository clientRepository;

    @Mock
    CaseMapper caseMapper;

    @Mock
    LoggingService loggingService;

    @Mock
    Principal principal;

    private User adminUser;
    private Client client;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setUserId(1L);
        adminUser.setUsername("admin");
        adminUser.setRoles(Set.of(new com.example.kromannreumert.user.entity.Role(1L, "ADMIN")));

        client = new Client();
        client.setId(1L);
        client.setName("Kromann Reumert");
    }

    // --------- getAllCases ---------
    @Test
    void getAllCases_returnsAllForNonJurist() {
        when(principal.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        Casee c1 = new Casee();
        Casee c2 = new Casee();
        when(caseRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Casee> result = caseService.getAllCases(principal);

        assertThat(result).hasSize(2);
        verify(caseRepository, times(1)).findAll();
    }

    @Test
    void getAllCases_returnsOnlyAssignedForJurist() {
        User jurist = new User();
        jurist.setUserId(4L);
        jurist.setUsername("jurist01");
        jurist.setRoles(Set.of(new com.example.kromannreumert.user.entity.Role(4L, "JURIST")));

        when(principal.getName()).thenReturn("jurist01");
        when(userRepository.findByUsername("jurist01")).thenReturn(Optional.of(jurist));

        Casee assignedCase = new Casee();
        when(caseRepository.findDistinctByUsers_UserId(4L)).thenReturn(List.of(assignedCase));

        List<Casee> result = caseService.getAllCases(principal);
        assertThat(result).hasSize(1);
        verify(caseRepository).findDistinctByUsers_UserId(4L);
    }

    // --------- getCaseByName ---------
    @Test
    void getCaseByName_returnsCase() {
        Casee c = new Casee();
        when(caseRepository.findAllByName("Contract")).thenReturn(Optional.of(c));
        Casee result = caseService.getCaseByName("Contract");
        assertThat(result).isEqualTo(c);
    }

    @Test
    void getCaseByName_throwsIfNotFound() {
        when(caseRepository.findAllByName("Missing")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> caseService.getCaseByName("Missing"));
    }

    // --------- createCase ---------
    @Test
    void createCase_createsAndLogs() {
        CaseRequestDTO dto = new CaseRequestDTO("New Case", 1L, Set.of(2L), 100L, 2);

        User responsible = new User();
        responsible.setUserId(2L);

        User assignee = new User();
        assignee.setUserId(2L);

        when(principal.getName()).thenReturn("admin");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(userRepository.findById(2)).thenReturn(Optional.of(assignee));
        when(userRepository.findById(2)).thenReturn(Optional.of(responsible));

        CaseResponseDTO response = caseService.createCase(dto, principal);

        assertThat(response.name()).isEqualTo("New Case");
        verify(caseRepository).save(any(Casee.class));
        verify(loggingService).log(eq(LogAction.CASE_CREATE), eq("admin"), anyString());
    }

    // --------- updateCase ---------
    @Test
    void updateCase_updatesAndLogs() {
        CaseUpdateRequest dto = new CaseUpdateRequest(1L, "Updated Case", 200L, 2, Set.of(2));

        Casee existing = new Casee();
        existing.setId(1L);
        existing.setUsers(new HashSet<>());

        User responsible = new User();
        responsible.setUserId(2L);

        User assignee = new User();
        assignee.setUserId(2L);

        when(caseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findById(2)).thenReturn(Optional.of(responsible));

        // Mock save() to return the same instance
        when(caseRepository.save(existing)).thenReturn(existing);

        // Map after save
        when(caseMapper.caseToResponse(existing))
                .thenReturn(new CaseResponseDTO("Updated Case", client, Set.of(assignee), 200L, responsible));

        when(principal.getName()).thenReturn("admin");

        CaseResponseDTO response = caseService.updateCase(dto, principal);

        assertThat(response.name()).isEqualTo("Updated Case");
        verify(caseRepository).save(existing);
        verify(loggingService).log(eq(LogAction.CASE_UPDATE), eq("admin"), anyString());
    }


    // --------- deleteCase ---------
    @Test
    void deleteCase_deletesAndLogs() {
        CaseDeleteRequestDTO dto = new CaseDeleteRequestDTO(1L);
        Casee existing = new Casee();
        existing.setId(1L);

        when(caseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(principal.getName()).thenReturn("admin");

        String result = caseService.deleteCase(dto, principal);

        assertThat(result).isEqualTo("Case deleted successfully");
        verify(caseRepository).delete(existing);
        verify(loggingService).log(eq(LogAction.CASE_DELETE), eq("admin"), anyString());
    }

    @Test
    void deleteCase_throwsIfNotFound() {
        CaseDeleteRequestDTO dto = new CaseDeleteRequestDTO(1L);
        when(caseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> caseService.deleteCase(dto, principal));
    }

}
