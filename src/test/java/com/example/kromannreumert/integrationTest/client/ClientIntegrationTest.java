package com.example.kromannreumert.integrationTest.client;

import com.example.kromannreumert.client.DTO.ClientRequestDTO;
import com.example.kromannreumert.client.DTO.UpdateClientIdPrefixDTO;
import com.example.kromannreumert.client.DTO.UpdateClientNameDTO;
import com.example.kromannreumert.user.entity.User;
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

    import static org.mockito.Mockito.*;
    import static org.junit.jupiter.api.Assertions.*;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
    import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ClientIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ApplicationContext context;

    @Autowired
    ObjectMapper objectMapper;

    private final String BASEURL = "/api/v1/client/";

    @Test
    void context() {
        assertNotNull(context);
    }


    @Test
    void should_return_allClients_for_AuthorizedUsers() throws Exception{
        String [] authorizedRoles = {"ADMIN", "SAGSBEHANDLER", "PARTNER"};
        for(String roles : authorizedRoles)
        {
            mockMvc.perform(get(BASEURL).with(user("ADMIN").roles(roles)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("Kromann Reumert"))
                    .andExpect(jsonPath("$[0].idPrefix").value(1000))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].name").value("AlphaSolution"))
                    .andExpect(jsonPath("$[1].idPrefix").value(2000));
        }
    }

    @Test
    void should_not_return_allClients_for_unAuthorizedUsers() throws Exception {
        mockMvc.perform(get(BASEURL).with(user("JURIST").roles("JURIST")))
                .andExpect(status().isForbidden());
    }

    @Test
    void should_return_oneClientWithId_for_AuthorizedUsers() throws Exception{
        String [] authorizedRoles = {"ADMIN", "SAGSBEHANDLER", "PARTNER"};
        for(String roles : authorizedRoles)
        {
            mockMvc.perform(get(BASEURL + "1000").with(user("ADMIN").roles(roles)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Kromann Reumert"))
                    .andExpect(jsonPath("$.idPrefix").value(1000));
        }
    }

    @Test
    void should_not_return_oneClient_for_unAuthorizedUsers() throws Exception {
        mockMvc.perform(get(BASEURL + "1000").with(user("JURIST").roles("JURIST")))
                .andExpect(status().isForbidden());
    }

    @Test
    void should_return_oneClientWithName_for_AuthorizedUsers() throws Exception{
        String [] authorizedRoles = {"ADMIN", "SAGSBEHANDLER", "PARTNER"};
        for(String roles : authorizedRoles)
        {
            mockMvc.perform(get(BASEURL + "/getclientbyname/AlphaSolution").with(user("ADMIN").roles(roles)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2))
                    .andExpect(jsonPath("$.name").value("AlphaSolution"))
                    .andExpect(jsonPath("$.idPrefix").value(2000));
        }
    }

    @Test
    void should_not_return_oneClientWithName_for_unAuthorizedUsers() throws Exception {
        mockMvc.perform(get(BASEURL + "/getclientbyname/AlphaSolution").with(user("JURIST").roles("JURIST")))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_can_create_client() throws Exception {
        var dto = new ClientRequestDTO("Enterprise-Admin", Set.of("admin"), 4000L);

        mockMvc.perform(post(BASEURL + "/add")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value("Client successfully created: " + dto.clientName()));

    }

    @Test
    void partner_can_create_client() throws Exception {
        var dto = new ClientRequestDTO("Enterprise-Partner", Set.of("admin"), 5000L);

        mockMvc.perform(post(BASEURL + "/add")
                        .with(user("admin").roles("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value("Client successfully created: " + dto.clientName()));

    }

    @Test
    void sagsbehandler_can_create_client() throws Exception {
        var dto = new ClientRequestDTO("Enterprise-Sags", Set.of("admin"), 6000L);

        mockMvc.perform(post(BASEURL + "/add")
                        .with(user("admin").roles("SAGSBEHANDLER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$").value("Client successfully created: " + dto.clientName()));
    }

    @Test
    void should_not_create_client_unAuthorized() throws Exception {
        var dto = new ClientRequestDTO("Enterprise-Sags", Set.of("admin"), 6000L);
        mockMvc.perform(post(BASEURL + "/add")
                        .with(user("admin").roles("JURIST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
    @Test
    void should_updateClientId_for_admin() throws Exception {

        var dto = new UpdateClientIdPrefixDTO("Kromann Reumert", 9999L);

            mockMvc.perform(patch(BASEURL + "/update/id")
                            .with(user("tester").roles("ADMIN"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value("Successfully updated client with: " + dto.idPrefix()));
    }

    @Test
    void should_updateClientId_with_partner() throws Exception {
        var DTO = new UpdateClientIdPrefixDTO("Kromann Reumert", 9998L);

        mockMvc.perform(patch(BASEURL + "/update/id").with(user("PARTNER").roles("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Successfully updated client with: " + DTO.idPrefix()));
    }

    @Test
    void should_updateClientId_with_sagsbehandler() throws Exception {
        var DTO = new UpdateClientIdPrefixDTO("Kromann Reumert", 9997L);

        mockMvc.perform(patch(BASEURL + "/update/id").with(user("SAGSBEHANDLER").roles("SAGSBEHANDLER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Successfully updated client with: " + DTO.idPrefix()));
    }

    @Test
    void should_not_updateClientId_with_jurist() throws Exception {
        var DTO = new UpdateClientIdPrefixDTO("Kromann Reumert", 9999L);

        mockMvc.perform(patch(BASEURL + "/update/id").with(user("ADMIN").roles("JURIST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void should_updateClientName_with_admin() throws Exception {
        var DTO = new UpdateClientNameDTO("Kromann Reumert", "Kromann");

        mockMvc.perform(patch(BASEURL + "/update/name").with(user("ADMIN").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Successfully updated client with: " + DTO.newName()));
    }

    @Test
    void should_updateClientName_with_partner() throws Exception {
        var DTO = new UpdateClientNameDTO("Kromann Reumert", "Kromann");

        mockMvc.perform(patch(BASEURL + "/update/name").with(user("PARTNER").roles("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Successfully updated client with: " + DTO.newName()));
    }

    @Test
    void should_updateClientName_with_sagsbehandler() throws Exception {
        var DTO = new UpdateClientNameDTO("Kromann Reumert", "Kromann");

        mockMvc.perform(patch(BASEURL + "/update/name").with(user("SAGSBEHANDLER").roles("SAGSBEHANDLER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Successfully updated client with: " + DTO.newName()));
    }

    @Test
    void should_not_updateClientName_with_jurist() throws Exception {
        var DTO = new UpdateClientNameDTO("Kromann Reumert", "Test");

        mockMvc.perform(patch(BASEURL + "/update/name").with(user("JURIST").roles("JURIST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void should_not_updateClientName_with_authorizedUsers_ifNameIsTheSame() throws Exception {
        var DTO = new UpdateClientNameDTO("Kromann Reumert", "Kromann Reumert");
        String[] authorizedRoles = {"ADMIN", "SAGSBEHANDLER", "PARTNER"};
        for (String roles : authorizedRoles) {
            mockMvc.perform(patch(BASEURL + "/update/name").with(user("ADMIN").roles(roles))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(DTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value("Cannot update the same name"));
        }
    }

    @Test
    void should_return_allClientsUsers_for_AuthorizedUsers() throws Exception{
        String [] authorizedRoles = {"ADMIN", "SAGSBEHANDLER", "PARTNER"};
        for(String roles : authorizedRoles)
        {
            mockMvc.perform(get(BASEURL + "user/1000").with(user("ADMIN").roles(roles)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value("Partner One"));
        }
    }

    @Test
    void should_not_return_allClientsUsers_for_AuthorizedUsers() throws Exception{
            mockMvc.perform(get(BASEURL + "user/1000").with(user("ADMIN").roles("JURIST")))
                    .andExpect(status().isForbidden());
    }

    @Test
    void should_return_deletedClient_for_AuthorizedUsers() throws Exception{
        String [] authorizedRoles = {"ADMIN", "SAGSBEHANDLER", "PARTNER"};
        for(String roles : authorizedRoles)
        {
            mockMvc.perform(delete(BASEURL + "delete/1").with(user("ADMIN").roles(roles)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value("Client with id: 1 has been deleted"));
        }
    }


    @Test
    void should_not_return_deletedClient_for_AuthorizedUsers() throws Exception{
            mockMvc.perform(delete(BASEURL + "delete/1").with(user("ADMIN").roles("JURIST")))
                    .andExpect(status().isForbidden());
    }




}

