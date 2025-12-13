package com.example.kromannreumert.unitTest.casee;

import com.example.kromannreumert.casee.controller.CaseController;
import com.example.kromannreumert.casee.dto.*;
import com.example.kromannreumert.casee.entity.Casee;
import com.example.kromannreumert.casee.service.CaseService;
import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.security.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CaseController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class CaseControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CaseService caseeService;

    @Autowired
    ObjectMapper objectMapper;

    // Need to add this after global exceptions has been created
    @MockitoBean
    private LoggingService loggingService;

    private final String BASE = "/api/v1/cases";

    // ===== GET ALL CASES =====
    @Test
    @WithMockUser(roles = {"ADMIN", "PARTNER", "SAGSBEHANDLER"})
    void should_returnAllCases_isOK_forAuthorizedRoles() throws Exception {

        Casee casee = new Casee();
        casee.setName("Case1");
        casee.setUsers(Collections.emptySet());
        casee.setIdPrefix(100L);

        when(caseeService.getAllCases(any(Principal.class)))
                .thenReturn(Collections.singletonList(casee));

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Case1"));

        verify(caseeService).getAllCases(any(Principal.class));
    }


    @Test
    @WithMockUser(roles = "JURIST")
    void should_returnAllCases_isOK_forJurist() throws Exception {

        when(caseeService.getAllCases(any(Principal.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk());

        verify(caseeService).getAllCases(any(Principal.class));
    }


    @Test
    void should_returnAllCases_isUnauthorized_forNoUser() throws Exception {
        mockMvc.perform(get(BASE))
                .andExpect(status().isUnauthorized());

        verify(caseeService, never()).getAllCases(any(Principal.class));
    }

    // ===== CREATE CASE =====
    @Test
    @WithMockUser(roles = {"ADMIN", "PARTNER", "SAGSBEHANDLER"})
    void should_createCase_isCreated_forAuthorizedRoles() throws Exception {
        CaseRequestDTO request = new CaseRequestDTO("New Case", 100L, Set.of(1, 2), 1000L, "testAdmin");
        CaseResponseDTO response = new CaseResponseDTO("New Case", null, Collections.emptySet(), 1000L, null);

        when(caseeService.createCase(eq(request), any(Principal.class))).thenReturn(response);

        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Case"));

        verify(caseeService).createCase(eq(request), any(Principal.class));
    }

    @Test
    @WithMockUser(roles = "JURIST")
    void should_createCase_isForbidden_forJurist() throws Exception {
        CaseRequestDTO request = new CaseRequestDTO("New Case", 100L, Set.of(1, 2), 1000L, "testAdmin");

        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(caseeService, never()).createCase(any(CaseRequestDTO.class), any(Principal.class));
    }

    @Test
    void should_createCase_isUnauthorized_forNoUser() throws Exception {
        CaseRequestDTO request = new CaseRequestDTO("New Case", 100L, Set.of(1, 2), 1000L, "testAdmin");

        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(caseeService, never()).createCase(any(CaseRequestDTO.class), any(Principal.class));
    }

    // ===== UPDATE CASE =====
    @Test
    @WithMockUser(roles = {"ADMIN", "PARTNER", "SAGSBEHANDLER"})
    void should_updateCase_isOK_forAuthorizedRoles() throws Exception {
        CaseUpdateRequest request = new CaseUpdateRequest(1L, "Updated Case", 2000L, "testAdmin", Set.of(2, 3));
        CaseResponseDTO response = new CaseResponseDTO("Updated Case", null, Collections.emptySet(), 2000L, null);

        when(caseeService.updateCase(eq(request), any(Principal.class))).thenReturn(response);

        mockMvc.perform(put(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Case"));

        verify(caseeService).updateCase(eq(request), any(Principal.class));
    }

    @Test
    @WithMockUser(roles = "JURIST")
    void should_not_updateCase_andReturnForbidden() throws Exception {

        CaseUpdateRequest dto =
                new CaseUpdateRequest(1L, "Updated Case", 2000L, "testJurist", Set.of(2, 3));

        // mock is not actually needed because service should not be called,
        // but we include it to mimic the ClientController test structure.
        when(caseeService.updateCase(eq(dto), any(Principal.class)))
                .thenReturn(null);

        mockMvc.perform(
                        put(BASE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isForbidden());

        verify(caseeService, never())
                .updateCase(any(CaseUpdateRequest.class), any(Principal.class));
    }


    @Test
    void should_updateCase_isUnauthorized_forNoUser() throws Exception {
        CaseUpdateRequest request = new CaseUpdateRequest(1L, "Updated Case", 2000L, "testJurist", Set.of(2, 3));

        mockMvc.perform(put(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(caseeService, never()).updateCase(any(CaseUpdateRequest.class), any(Principal.class));
    }

    // ===== DELETE CASE =====
    @Test
    @WithMockUser(roles = {"ADMIN", "PARTNER", "SAGSBEHANDLER"})
    void should_deleteCase_isOK_forAuthorizedRoles() throws Exception {
        CaseDeleteRequestDTO request = new CaseDeleteRequestDTO(100L);

        when(caseeService.deleteCase(eq(request), any(Principal.class))).thenReturn("Case deleted successfully");

        mockMvc.perform(delete(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Case deleted successfully"));

        verify(caseeService).deleteCase(eq(request), any(Principal.class));
    }

    @Test
    @WithMockUser(roles = "JURIST")
    void should_deleteCase_isForbidden_forJurist() throws Exception {
        CaseDeleteRequestDTO request = new CaseDeleteRequestDTO(100L);

        mockMvc.perform(delete(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(caseeService, never()).deleteCase(any(CaseDeleteRequestDTO.class), any(Principal.class));
    }

    @Test
    void should_deleteCase_isUnauthorized_forNoUser() throws Exception {
        CaseDeleteRequestDTO request = new CaseDeleteRequestDTO(100L);

        mockMvc.perform(delete(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(caseeService, never()).deleteCase(any(CaseDeleteRequestDTO.class), any(Principal.class));
    }
}
