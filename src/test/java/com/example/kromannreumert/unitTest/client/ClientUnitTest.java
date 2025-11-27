package com.example.kromannreumert.unitTest.client;

import com.example.kromannreumert.client.DTO.ClientResponeDTO;
import com.example.kromannreumert.client.DTO.UpdateClientIdPrefixDTO;
import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.client.mapper.ClientMapper;
import com.example.kromannreumert.client.repository.ClientRepository;
import com.example.kromannreumert.client.service.ClientService;
import com.example.kromannreumert.user.entity.Role;
import com.example.kromannreumert.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.sql.Date;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class ClientUnitTest {


    @Mock
    ClientRepository clientRepository;

    @Mock
    ClientMapper clientMapper;

    @InjectMocks
    ClientService clientService;

    @Mock
    ClientResponeDTO clientResponeDTO;

    @Test
    void getAllClients() {

    }

    @Test
    void getClientByIdPrefix() {

        // ARRANGE
        Date now = new Date(2025-11-25);
        Long idPrefix = 1000L;
        String clientName = "ClientTestName";
        Set<User> addUsers = Set.of(
                new User
                        (0L, "test", "test", "test","test", now, Set.of(new Role(1L, "ADMIN"))));
        Client addClients = new Client(1L,clientName, addUsers,idPrefix);
        ClientResponeDTO test = new ClientResponeDTO(addClients.getId(), addClients.getName(), List.of("hej"), addClients.getIDPrefix());

        // ACT
        when(clientRepository.getClientByIDPrefix(1000L)).thenReturn(Optional.of(addClients));
        when(clientMapper.toClientDTO(addClients)).thenReturn(test);
        ClientResponeDTO result = clientService.getClientByIdPrefix(1000L);

        // ASSERT
        assertNotNull(result);
        verify(clientRepository).getClientByIDPrefix(idPrefix);
    }

    @Test
    void getClientByName() {

//        // ARRANGE
//        Long idPrefix = 1000L;
//        String clientName = "ClientTestName";
//        Set<User> addUsers = Set.of(
//                new User
//                        (0L, "test", "test", "test","test", null, null));
//        Client addClients = new Client(1L,clientName, addUsers,idPrefix);
//
//        // ACT
//        when(clientRepository.findClientByName("ClientTestName")).thenReturn(Optional.of(addClients));
//        Client result = clientService.getClientByName("ClientTestName");
//
//        // ASSERT
//        assertNotNull(result);
//        assertEquals(idPrefix, result.getIDPrefix());
//        assertEquals(clientName, result.getName());
//        verify(clientRepository).findClientByName("ClientTestName");

    }

    @Test
    void addClient() {


//        // ARRANGE
//        String clientName = "ClientTest";
//        Long idPrefix = 99000L;
//        Set<User> addUsers = Set.of(new User(0L, "test", "test", "test","test", null, null));
//        CreateClientDTO addClient = new CreateClientDTO(clientName, addUsers, idPrefix);
//        Client client = new Client(null, addClient.clientName(), addClient.users(), addClient.idPrefix());
//
//        // ACT
//        when(clientRepository.save(any(Client.class))).thenReturn(client);
//        when(clientRepository.findClientByName(client.getName())).thenReturn(Optional.of(client));
//
//
//        String returnResult = clientService.addClient(addClient);
//        Client result = clientService.getClientByName(clientName);
//        String expectedResult = "Client successfully created: " + clientName;
//
//        // ASSERT
//        assertNotNull(returnResult);
//        assertNotNull(result);
//        assertEquals(clientName, result.getName());
//        assertEquals(expectedResult, returnResult);
//        verify(clientRepository).save(any(Client.class));
//        verify(clientRepository).findClientByName(clientName);
    }

    @Test
    void updateClientName() {

//        // ARRANGE
//        String clientName = "ClientTest";
//        UpdateClientNameDTO updateName = new UpdateClientNameDTO("UpdatedClientName");
//
//        Client client = new Client(null, clientName, Set.of(), 99000L);
//        Client updatedClient = new Client(null, updateName.name(), client.getUsers(), client.getIDPrefix());
//
//        when(clientRepository.findClientByName(clientName))
//                .thenReturn(Optional.of(client));
//
//        when(clientRepository.save(any(Client.class)))
//                .thenReturn(updatedClient);
//
//        // ACT
//        Client result = clientService.updateClientName(updateName, );
//
//        // ASSERT
//        assertEquals(updateName.name(), result.getName());
//        assertNotNull(result);
//
//        verify(clientRepository).findClientByName(clientName);
//        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void updateClientIdPrefix() {
        Long idPrefix = 9000L;
        String clientName = "TestClientName";
        UpdateClientIdPrefixDTO updateID = new UpdateClientIdPrefixDTO(clientName,9900L);

        Client client = new Client(null, clientName, Set.of(), idPrefix);
        Client updatedClient = new Client(null, clientName, client.getUsers(), updateID.idPrefix());

        when(clientRepository.findClientByName(clientName)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(updatedClient);

        String result = clientService.updateClientIdPrefix(updateID);

        assertNotNull(result);
        assertNotEquals(idPrefix, updateID.idPrefix());
        assertEquals(result, "Successfully updated client with: " + updateID.idPrefix());
        verify(clientRepository).findClientByName(clientName);
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void deleteClient() {
        String clientName = "DeleteTest";
        Long idPrefix = 9000L;
        Long id = 1L;
        Client client = new Client(id, clientName, Set.of(), idPrefix);

        when(clientRepository.findClientByName(clientName)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        clientRepository.save(client);

        doNothing().when(clientRepository).deleteById(id);

        Optional<Client> findClient = clientRepository.findClientByName(clientName);
        Long resultId = findClient.get().getId();
        assertNotNull(findClient);
        String expectedResult = "Client with id: " + resultId + " has been deleted";

        String result = clientService.deleteClient(resultId);

        assertNotNull(result);
        assertEquals(result, expectedResult);
        verify(clientRepository).deleteById(id);


    }

}
