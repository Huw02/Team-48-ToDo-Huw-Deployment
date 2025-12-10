package com.example.kromannreumert.integrationTest.casee;

import com.example.kromannreumert.casee.dto.CaseDeleteRequestDTO;
import com.example.kromannreumert.casee.dto.CaseRequestDTO;
import com.example.kromannreumert.casee.dto.CaseUpdateRequest;
import com.example.kromannreumert.client.repository.ClientRepository;
import com.example.kromannreumert.logging.entity.Logging;
import com.example.kromannreumert.logging.service.LoggingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class CaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private final String BASEURL = "/api/v1/cases";

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LoggingService loggingService;



    // GET cases
    @Test
    void authorizedUsersCanGetAllCases() throws Exception {
        String[][] users = {{"admin", "ADMIN"}, {"partner01", "PARTNER"}, {"worker01", "SAGSBEHANDLER"}};

        for (String[] u : users) {
            mockMvc.perform(get(BASEURL).with(user(u[0]).roles(u[1])))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void juristCanOnlySeeAssignedCases() throws Exception {
        mockMvc.perform(get(BASEURL).with(user("jurist01").roles("JURIST")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Contract Review"));
    }



    // CREATE cases
    @Test
    void adminCanCreateCase() throws Exception {
        var dto = new CaseRequestDTO(
                "Enterprise-Case",
                1L,
                Set.of(2),
                3300L,
                "admin" // responsibleUserId
        );

        mockMvc.perform(post(BASEURL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(dto.name()))
                .andExpect(jsonPath("$.idPrefix").value(dto.idPrefix()))
                .andExpect(jsonPath("$.responsibleUser.username").value(dto.responsibleUsername()))
                .andExpect(jsonPath("$.users[0].userId").value(2));
    }

    @Test
    void partnerCanCreateCase() throws Exception {
        var dto = new CaseRequestDTO(
                "Partner-Created-Case",
                1L,
                Set.of(2),
                4400L,
                "partner01"
        );

        mockMvc.perform(post(BASEURL)
                        .with(user("partner01").roles("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(dto.name()))
                .andExpect(jsonPath("$.idPrefix").value(dto.idPrefix()))
                .andExpect(jsonPath("$.responsibleUser.username").value(dto.responsibleUsername()))
                .andExpect(jsonPath("$.users[0].userId").value(2));
    }

    @Test
    void sagsbehandlerCanCreateCase() throws Exception {
        var dto = new CaseRequestDTO(
                "Sagsbehandler-Created-Case",
                1L,
                Set.of(3),
                5500L,
                "worker01"
        );

        mockMvc.perform(post(BASEURL)
                        .with(user("worker01").roles("SAGSBEHANDLER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(dto.name()))
                .andExpect(jsonPath("$.idPrefix").value(dto.idPrefix()))
                .andExpect(jsonPath("$.responsibleUser.username").value(dto.responsibleUsername()))
                .andExpect(jsonPath("$.users[0].userId").value(3));
    }

    @Test
    void juristCannotCreateCase() throws Exception {
        var dto = new CaseRequestDTO(
                "Jurist-Created-Case",
                1L,
                Set.of(4),
                6600L,
                "testJurist"
        );

        mockMvc.perform(post(BASEURL)
                        .with(user("jurist01").roles("JURIST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    // UPDATE cases
    @Test
    void adminCanUpdateCase() throws Exception {
        var dto = new CaseUpdateRequest(
                1L,
                "Updated-Enterprise-Case",
                3310L,
                "admin",
                Set.of(2, 3)
        );

        mockMvc.perform(put(BASEURL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(dto.name()))
                .andExpect(jsonPath("$.idPrefix").value(dto.idPrefix()))
                .andExpect(jsonPath("$.responsibleUser.username").value(dto.responsibleUsername()))
                .andExpect(jsonPath("$.users.length()").value(dto.assigneeIds().size()));
    }

    @Test
    void partnerCanUpdateCase() throws Exception {
        var dto = new CaseUpdateRequest(
                2L,
                "Updated-Partner-Case",
                4410L,
                "partner01",
                Set.of(2)
        );

        mockMvc.perform(put(BASEURL)
                        .with(user("partner01").roles("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(dto.name()))
                .andExpect(jsonPath("$.idPrefix").value(dto.idPrefix()))
                .andExpect(jsonPath("$.responsibleUser.username").value(dto.responsibleUsername()))
                .andExpect(jsonPath("$.users[0].userId").value(2));
    }

    @Test
    void sagsbehandlerCanUpdateCase() throws Exception {
        var dto = new CaseUpdateRequest(
                1L,
                "Updated-Sagsbehandler-Case",
                5510L,
                "worker01",
                Set.of(3)
        );

        mockMvc.perform(put(BASEURL)
                        .with(user("worker01").roles("SAGSBEHANDLER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(dto.name()))
                .andExpect(jsonPath("$.idPrefix").value(dto.idPrefix()))
                .andExpect(jsonPath("$.responsibleUser.username").value(dto.responsibleUsername()))
                .andExpect(jsonPath("$.users[0].userId").value(3));
    }

    @Test
    void juristCannotUpdateCase() throws Exception {
        var dto = new CaseUpdateRequest(
                1L,
                "Jurist-Update-Attempt",
                6601L,
                "jurist01",
                Set.of(4)
        );

        mockMvc.perform(put(BASEURL)
                        .with(user("jurist01").roles("JURIST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    // DELETE CASE TESTS
    @Test
    void adminCanDeleteCase() throws Exception {
        var request = new CaseDeleteRequestDTO(1L);

        mockMvc.perform(delete(BASEURL)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Case deleted successfully"));
    }

    @Test
    void partnerCanDeleteCase() throws Exception {
        var request = new CaseDeleteRequestDTO(2L);

        mockMvc.perform(delete(BASEURL)
                        .with(user("partner01").roles("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Case deleted successfully"));
    }

    @Test
    void sagsbehandlerCanDeleteCase() throws Exception {
        var request = new CaseDeleteRequestDTO(1L);

        mockMvc.perform(delete(BASEURL)
                        .with(user("worker01").roles("SAGSBEHANDLER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Case deleted successfully"));
    }

    @Test
    void juristCannotDeleteCase() throws Exception {
        var request = new CaseDeleteRequestDTO(1L);

        mockMvc.perform(delete(BASEURL)
                        .with(user("jurist01").roles("JURIST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

}
