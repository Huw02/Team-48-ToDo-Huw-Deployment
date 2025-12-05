package com.example.kromannreumert.unitTest.client;

import com.example.kromannreumert.client.DTO.ClientRequestDTO;
import com.example.kromannreumert.client.DTO.ClientResponeDTO;
import com.example.kromannreumert.client.DTO.UpdateClientIdPrefixDTO;
import com.example.kromannreumert.client.DTO.UpdateClientNameDTO;
import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.client.mapper.ClientMapper;
import com.example.kromannreumert.client.repository.ClientRepository;
import com.example.kromannreumert.client.service.ClientService;
import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.user.entity.Role;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class ClientUnitTest {

    @Mock
    ClientRepository clientRepository;

    @Mock
    ClientMapper clientMapper;

    @Mock
    UserRepository userRepository;

    @Mock
    LoggingService loggingService;

    @InjectMocks
    ClientService clientService;


    @Test
    void should_return_all_clients() {

        // ARRANGE
        Long idPrefix = 1000L;
        String clientName = "ClientTestName";
        Client createClient = new Client(1L, clientName, null, idPrefix);
        ClientResponeDTO convert = new ClientResponeDTO(1L, clientName, null, idPrefix);

        when(clientRepository.findAll()).thenReturn(List.of(createClient));
        when(clientMapper.toClientDTO(createClient)).thenReturn(convert);

        // ACT
        List<ClientResponeDTO> result = clientService.getAllClients("Abdi");

        // ASSERT
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(createClient.getName(), result.getFirst().name());
        assertEquals(createClient.getIDPrefix(), result.getFirst().idPrefix());
        verify(clientRepository).findAll();
        verify(clientMapper).toClientDTO(createClient);
        verify(loggingService).log(eq(LogAction.VIEW_ALL_CLIENTS), eq("Abdi"), eq("Fetched all clients"));

    }

    @Test
    void should_return_converted_client_to_DTO() {

        // ARRANGE
        LocalDateTime now = LocalDateTime.now();
        Long idPrefix = 1000L;
        String clientName = "ClientTestName";
        Set<User> addUsers = Set.of(
                new User
                        (0L, "test", "test", "test","test", now, Set.of(new Role(1L, "ADMIN"))));
        Client createClient = new Client(1L,clientName, addUsers,idPrefix);
        ClientResponeDTO convertClient = new ClientResponeDTO(createClient.getId(), createClient.getName(), List.of("hej"), createClient.getIDPrefix());

        // ACT
        when(clientRepository.getClientByIDPrefix(1000L)).thenReturn(Optional.of(createClient));
        when(clientMapper.toClientDTO(createClient)).thenReturn(convertClient);
        ClientResponeDTO result = clientService.getClientByIdPrefix(1000L, "Tester");

        // ASSERT
        assertNotNull(result);
        assertEquals(createClient.getName(), result.name());
        assertEquals(createClient.getIDPrefix(), result.idPrefix());
        verify(clientRepository).getClientByIDPrefix(idPrefix);
        verify(clientMapper).toClientDTO(createClient);
        verify(loggingService).log(eq(LogAction.VIEW_ONE_CLIENT), eq("Tester"), eq("Viewed client with prefix: " + idPrefix));
    }

    @Test
    void should_return_client_by_clientName() {

        // ARRANGE
        Long idPrefix = 1000L;
        String clientName = "ClientTestName";
        Client createClient = new Client(1L, clientName, null, idPrefix);
        ClientResponeDTO convertClient = new ClientResponeDTO(createClient.getId(), createClient.getName(), null, createClient.getIDPrefix());

        when(clientRepository.findClientByName(clientName)).thenReturn(Optional.of(createClient));
        when(clientMapper.toClientDTO(createClient)).thenReturn(convertClient);

        // ACT
        ClientResponeDTO result = clientService.getClientByName("ClientTestName", "Tester");

        // ASSERT
        assertNotNull(result);
        assertEquals(createClient.getName(), result.name());
        assertEquals(createClient.getIDPrefix(), result.idPrefix());
        assertEquals(createClient.getId(), result.id());
        verify(clientRepository).findClientByName("ClientTestName");
        verify(clientMapper).toClientDTO(createClient);
        verify(loggingService).log(eq(LogAction.VIEW_ONE_CLIENT), eq("Tester"), contains("ClientTestName"));
    }

    @Test
    void should_return_user_from_client() {
        // TODO Add unit test for this method
    }

    @Test
    void should_add_client_and_return_success() {

        // ARRANGE
        String clientName = "ClientTest";
        Long idPrefix = 99000L;
        User createUser = new User(1L,"TestUser", "Test", "Test@1234", "1234", null, null);
        Set<String> users = Set.of(createUser.getUsername());
        ClientRequestDTO createClient = new ClientRequestDTO(clientName, users, idPrefix);
        Client convertDTOToClient = new Client(null, createClient.clientName(), null, createClient.idPrefix());

        when(clientRepository.save(any(Client.class))).thenReturn(convertDTOToClient);
        when(userRepository.findByUsername("TestUser")).thenReturn(Optional.of(createUser));

        // ACT
        String result = clientService.addClient(createClient, "Creator");
        String expectedResult = "Client successfully created: " + createClient.clientName();


        // ASSERT
        assertNotNull(result);
        assertEquals(result, expectedResult);
        verify(clientRepository).save(any(Client.class));
        verify(userRepository).findByUsername("TestUser");
        verify(loggingService).log(eq(LogAction.CREATE_CLIENT), eq("Creator"), contains(createClient.clientName()));

    }

    @Test
    void should_update_client_name() {

        // ARRANGE
        UpdateClientNameDTO updateName = new UpdateClientNameDTO("hi", "UpdatedClientName");
        Client oldClient = new Client(1L, updateName.oldName(), null, null);
        Client updatedClient = new Client (oldClient.getId(), updateName.newName(), oldClient.users, oldClient.getIDPrefix());

        when(clientRepository.findClientByName(oldClient.getName())).thenReturn(Optional.of(oldClient));
        when(clientRepository.save(any(Client.class))).thenReturn(updatedClient);

        // ACT
        String result = clientService.updateClientName(updateName, "Updater");
        ArgumentCaptor<Client> newNameCaptor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(newNameCaptor.capture());
        String expectedName = newNameCaptor.getValue().getName();
        String expectedResult = "Successfully updated client with: " + expectedName;

        // ASSERT
        assertNotNull(result);
        assertEquals(result, expectedResult);
        assertNotEquals(expectedName, updateName.oldName());
        verify(clientRepository).findClientByName(updateName.oldName());
        verify(clientRepository).save(any(Client.class));
        verify(loggingService).log(eq(LogAction.UPDATE_CLIENT), eq("Updater"), contains(expectedName));

    }

    @Test
    void should_update_client_idPrefix() {

        // ARRANGE
        Long idPrefix = 9000L;
        String clientName = "TestClientName";
        UpdateClientIdPrefixDTO updateID = new UpdateClientIdPrefixDTO(clientName,9900L);

        Client client = new Client(null, clientName, Set.of(), idPrefix);
        Client updatedClient = new Client(null, clientName, client.getUsers(), updateID.idPrefix());

        when(clientRepository.findClientByName(clientName)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(updatedClient);

        // ACT
        String result = clientService.updateClientIdPrefix(updateID, "Updater");


        // ASSERT
        assertNotNull(result);
        assertNotEquals(idPrefix, updateID.idPrefix());
        assertEquals(result, "Successfully updated client with: " + updateID.idPrefix());
        verify(clientRepository).findClientByName(clientName);
        verify(clientRepository).save(any(Client.class));
        verify(loggingService).log(eq(LogAction.UPDATE_CLIENT), eq("Updater"), eq("Updated client prefix"));
    }


    @Test
    void should_delete_client() {

        // ARRANGE
        String clientName = "DeleteTest";
        Long idPrefix = 9000L;
        Long id = 1L;
        Client client = new Client(id, clientName, Set.of(), idPrefix);

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        doNothing().when(clientRepository).deleteById(id);

        // ACT
        String expectedResult = "Client with id: " + id + " has been deleted";

        // ASSERT
        String result = clientService.deleteClient(id, "Remover");
        assertNotNull(result);
        assertEquals(result, expectedResult);
        verify(clientRepository).deleteById(id);
        verify(loggingService).log(eq(LogAction.DELETE_CLIENT), eq("Remover"), contains(id.toString()));
        verify(clientRepository).findById(id);
    }


}
