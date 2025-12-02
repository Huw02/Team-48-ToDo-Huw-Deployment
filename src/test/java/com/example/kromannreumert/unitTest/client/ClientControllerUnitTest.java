package com.example.kromannreumert.unitTest.client;

import com.example.kromannreumert.client.DTO.*;
import com.example.kromannreumert.client.controller.ClientController;
import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.client.service.ClientService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClientController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")

public class ClientControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ClientService clientService;

    @Autowired
    ObjectMapper objectMapper;

    private final String BASE = "/api/v1/client/";

    @Test
    @WithMockUser(roles = "ADMIN")
    void should_returnAllClients_isOK_for_Admin() throws Exception {
        ClientResponeDTO dto = new ClientResponeDTO(1L, "TestClient", null, 1000L);

        when(clientService.getAllClients(anyString())).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].name").value("TestClient"));

        verify(clientService).getAllClients(anyString());
    }


    @Test
    @WithMockUser(roles = "SAGSBEHANDLER")
    void should_returnAllClients_isOK_for_sagsbehandler() throws Exception {
        ClientResponeDTO dto = new ClientResponeDTO(1L, "TestClient", null, 1000L);

        when(clientService.getAllClients(anyString())).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].name").value("TestClient"));

        verify(clientService).getAllClients(anyString());
    }

    @Test
    @WithMockUser(roles = "PARTNER")
    void should_returnAllClients_isOK_for_partner() throws Exception {
        ClientResponeDTO dto = new ClientResponeDTO(1L, "TestClient", null, 1000L);

        when(clientService.getAllClients(anyString())).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].name").value("TestClient"));


        verify(clientService).getAllClients(anyString());
    }

    @Test
    @WithMockUser(roles = "JURIST")
    void should_returnAllClients_isForbidden_for_jurist() throws Exception {
        mockMvc.perform(get(BASE))
                .andExpect(status().isForbidden());
        verify(clientService, never()).getAllClients(anyString());

    }

    @Test
    void should_returnAllClients_isUnAuthorized_for_noOne() throws Exception {
        mockMvc.perform(get(BASE))
                .andExpect(status().isUnauthorized());
        verify(clientService, never()).getAllClients(anyString());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void should_returnOneClientWithId_isOK_for_Admin() throws Exception {
        ClientResponeDTO dto = new ClientResponeDTO(1L, "TestClient", null, 1000L);

        when(clientService.getClientByIdPrefix(eq(1000L), anyString()))
                .thenReturn(dto);

        mockMvc.perform(get(BASE + "/1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("TestClient"));

        verify(clientService).getClientByIdPrefix(eq(1000L), anyString());
    }


    @Test
    @WithMockUser(roles = "SAGSBEHANDLER")
    void should_returnOneClientWithId_isOK_for_sagsbehandler() throws Exception {
        ClientResponeDTO dto = new ClientResponeDTO(1L, "TestClient", null, 1000L);

        when(clientService.getClientByIdPrefix(eq(1000L), anyString()))
                .thenReturn(dto);

        mockMvc.perform(get(BASE + "/1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("TestClient"));

        verify(clientService).getClientByIdPrefix(eq(1000L), anyString());
    }

    @Test
    @WithMockUser(roles = "PARTNER")
    void should_returnOneClientWithId_isOK_for_partner() throws Exception {
        ClientResponeDTO dto = new ClientResponeDTO(1L, "TestClient", null, 1000L);

        when(clientService.getClientByIdPrefix(eq(1000L), anyString()))
                .thenReturn(dto);

        mockMvc.perform(get(BASE + "/1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("TestClient"));

        verify(clientService).getClientByIdPrefix(eq(1000L), anyString());
    }

    @Test
    @WithMockUser(roles = "JURIST")
    void should_returnOneClientWithId_isForbidden_for_jurist() throws Exception {
        mockMvc.perform(get(BASE + "/1"))
                .andExpect(status().isForbidden());
        verify(clientService, never()).getClientByIdPrefix(anyLong(), anyString());
    }

    @Test
    void should_returnOneClientWithId_isUnAuthorized_for_noOne() throws Exception {
        mockMvc.perform(get(BASE + "/1"))
                .andExpect(status().isUnauthorized());
        verify(clientService, never()).getClientByIdPrefix(anyLong(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void should_returnOneClientWithName_isOK_for_Admin() throws Exception {
        ClientResponeDTO dto = new ClientResponeDTO(1L, "TestClient", null, 1000L);

        when(clientService.getClientByName(eq("TestClient"), anyString()))
                .thenReturn(dto);

        mockMvc.perform(get(BASE + "/getclientbyname/TestClient"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("TestClient"));

        verify(clientService).getClientByName(eq("TestClient"), anyString());
    }


    @Test
    @WithMockUser(roles = "SAGSBEHANDLER")
    void should_returnOneClientWithName_isOK_for_sagsbehandler() throws Exception {
        ClientResponeDTO dto = new ClientResponeDTO(1L, "TestClient", null, 1000L);

        when(clientService.getClientByName(eq("TestClient"), anyString()))
                .thenReturn(dto);

        mockMvc.perform(get(BASE + "/getclientbyname/TestClient"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("TestClient"));

        verify(clientService).getClientByName(eq("TestClient"), anyString());
    }

    @Test
    @WithMockUser(roles = "PARTNER")
    void should_returnOneClientWithName_isOK_for_partner() throws Exception {
        ClientResponeDTO dto = new ClientResponeDTO(1L, "TestClient", null, 1000L);

        when(clientService.getClientByName(eq("TestClient"), anyString()))
                .thenReturn(dto);

        mockMvc.perform(get(BASE + "/getclientbyname/TestClient"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("TestClient"));

        verify(clientService).getClientByName(eq("TestClient"), anyString());
    }

    @Test
    @WithMockUser(roles = "JURIST")
    void should_returnOneClientWithName_isForbidden_for_jurist() throws Exception {
        mockMvc.perform(get(BASE + "/getclientbyname/TestClient"))
                .andExpect(status().isForbidden());

        verify(clientService, never()).getClientByName(anyString(), anyString());
    }

    @Test
    void should_returnOneClientWithName_isUnAuthorized_for_noOne() throws Exception {
        mockMvc.perform(get(BASE + "/getclientbyname/TestClient"))
                .andExpect(status().isUnauthorized());
        verify(clientService, never()).getClientByName(anyString(), anyString());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void should_return_addClient_successfully_for_admin() throws Exception {
        ClientRequestDTO test = new ClientRequestDTO("test", Set.of("hey"),1000L);
        when(clientService.addClient(eq(test), anyString())).thenReturn("Client successfully created: " + test.clientName());

        mockMvc.perform(MockMvcRequestBuilders.post(BASE +"/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(test)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$").value("Client successfully created: " + test.clientName()));

        verify(clientService).addClient(eq(test), anyString());
    }

    @Test
    @WithMockUser(roles = "PARTNER")
    void should_return_addClient_successfully_for_partner() throws Exception {
        ClientRequestDTO test = new ClientRequestDTO("test", Set.of("hey"),1000L);
        when(clientService.addClient(eq(test), anyString())).thenReturn("Client successfully created: " + test.clientName());

        mockMvc.perform(MockMvcRequestBuilders.post(BASE +"/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(test)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value("Client successfully created: " + test.clientName()));

        verify(clientService).addClient(eq(test), anyString());
    }

    @Test
    @WithMockUser(roles = "SAGSBEHANDLER")
    void should_return_addClient_successfully_for_sagsbehandler() throws Exception {
        ClientRequestDTO test = new ClientRequestDTO("test", Set.of("hey"),1000L);
        when(clientService.addClient(eq(test), anyString())).thenReturn("Client successfully created: " + test.clientName());

        mockMvc.perform(MockMvcRequestBuilders.post(BASE +"/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(test)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value("Client successfully created: " + test.clientName()));

        verify(clientService).addClient(eq(test), anyString());
    }

    @Test
    @WithMockUser(roles = "JURIST")
    void should_return_addClient_forbidden_for_jurist() throws Exception {
        ClientRequestDTO test = new ClientRequestDTO("test", Set.of("hey"),1000L);
        when(clientService.addClient(eq(test), anyString())).thenReturn("Client successfully created: " + test.clientName());

        mockMvc.perform(MockMvcRequestBuilders.post(BASE +"/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(test)))
                .andExpect(status().isForbidden());

        verify(clientService, never()).addClient(any(ClientRequestDTO.class), anyString());

    }

    @Test
    void should_return_addClient_unauthorized_for_noRole() throws Exception {
        ClientRequestDTO test = new ClientRequestDTO("test", Set.of("hey"),1000L);
        when(clientService.addClient(eq(test), anyString())).thenReturn("Client successfully created: " + test.clientName());

        mockMvc.perform(MockMvcRequestBuilders.post(BASE +"/add")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnauthorized());
        verify(clientService, never()).addClient(any(ClientRequestDTO.class), anyString());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void should_return_deleted_client_IsOK_for_Admin() throws Exception {
        when(clientService.deleteClient(eq(100L), anyString())).thenReturn("Client with id: 100L has been deleted");

        mockMvc.perform(delete(BASE + "/delete/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Client with id: 100L has been deleted"));

        verify(clientService).deleteClient(eq(100L), anyString());
    }

    @Test
    @WithMockUser(roles = "SAGSBEHANDLER")
    void should_return_deleted_client_IsOK_for_sagsbehandler() throws Exception {
        when(clientService.deleteClient(eq(100L), anyString())).thenReturn("Client with id: 100L has been deleted");

        mockMvc.perform(delete(BASE + "/delete/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Client with id: 100L has been deleted"));

        verify(clientService).deleteClient(eq(100L), anyString());
    }

    @Test
    @WithMockUser(roles = "PARTNER")
    void should_return_deleted_client_IsOK_for_partner() throws Exception {
        when(clientService.deleteClient(eq(100L), anyString())).thenReturn("Client with id: 100L has been deleted");

        mockMvc.perform(delete(BASE + "/delete/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Client with id: 100L has been deleted"));

        verify(clientService).deleteClient(eq(100L), anyString());
    }

    @Test
    @WithMockUser(roles = "JURIST")
    void should_return_deleted_client_IsForbidden_for_Admin() throws Exception {
        when(clientService.deleteClient(eq(100L), anyString())).thenReturn("Client with id: 100L has been deleted");

        mockMvc.perform(delete(BASE + "/delete/100"))
                .andExpect(status().isForbidden());

        verify(clientService, never()).deleteClient(anyLong(), anyString());
    }

    @Test
    void should_return_deleted_client_IsUnAuthorized_for_noUser() throws Exception {
        when(clientService.deleteClient(eq(100L), anyString())).thenReturn("Client with id: 100L has been deleted");

        mockMvc.perform(delete(BASE + "/delete/100"))
                .andExpect(status().isUnauthorized());

        verify(clientService, never()).deleteClient(anyLong(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void should_updateClientIdPrefix_andReturnOK() throws Exception {

        UpdateClientIdPrefixDTO dto =
                new UpdateClientIdPrefixDTO("ClientA", 9000L);

        when(clientService.updateClientIdPrefix(eq(dto), anyString()))
                .thenReturn("Updated ID prefix");

        mockMvc.perform(
                        patch("/api/v1/client/update/id")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Updated ID prefix"));

        verify(clientService).updateClientIdPrefix(eq(dto), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void should_updateClientName_andReturnOK() throws Exception {

        UpdateClientNameDTO dto =
                new UpdateClientNameDTO("OldName", "NewName");

        when(clientService.updateClientName(eq(dto), anyString()))
                .thenReturn("Updated client name");

        mockMvc.perform(
                        patch("/api/v1/client/update/name")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Updated client name"));

        verify(clientService).updateClientName(eq(dto), anyString());
    }

    // ALL TEST DONE
    @Test
    @WithMockUser(roles = "ADMIN")
    void should_updateClientUsers_andReturnOK() throws Exception {

        UpdateClientUserList dto =
                new UpdateClientUserList(9000L, Set.of("User1", "User2"));

        when(clientService.updateClientUserList(eq(dto), anyString()))
                .thenReturn("Updated user list");

        mockMvc.perform(
                        put("/api/v1/client/update/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Updated user list"));

        verify(clientService).updateClientUserList(eq(dto), anyString());
    }

    @Test
    @WithMockUser(roles = "PARTNER")
    void should_updateClientUsers_andReturnOK_partner() throws Exception {

        UpdateClientUserList dto =
                new UpdateClientUserList(9000L, Set.of("User1", "User2"));

        when(clientService.updateClientUserList(eq(dto), anyString()))
                .thenReturn("Updated user list");

        mockMvc.perform(
                        put("/api/v1/client/update/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Updated user list"));

        verify(clientService).updateClientUserList(eq(dto), anyString());
    }

    @Test
    @WithMockUser(roles = "SAGSBEHANDLER")
    void should_updateClientUsers_andReturnOK_sagsbehandler() throws Exception {

        UpdateClientUserList dto =
                new UpdateClientUserList(9000L, Set.of("User1", "User2"));

        when(clientService.updateClientUserList(eq(dto), anyString()))
                .thenReturn("Updated user list");

        mockMvc.perform(
                        put("/api/v1/client/update/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Updated user list"));

        verify(clientService).updateClientUserList(eq(dto), anyString());
    }

    @Test
    @WithMockUser(roles = "JURIST")
    void should_not_updateClientUsers_andReturnIsForbidden() throws Exception {

        UpdateClientUserList dto =
                new UpdateClientUserList(9000L, Set.of("User1", "User2"));

        when(clientService.updateClientUserList(eq(dto), anyString()))
                .thenReturn("Updated user list");

        mockMvc.perform(
                        put("/api/v1/client/update/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isForbidden());
        verify(clientService, never()).updateClientUserList(any(UpdateClientUserList.class), anyString());
    }
}
