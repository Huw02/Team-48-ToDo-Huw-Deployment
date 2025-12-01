package com.example.kromannreumert.client.service;

import com.example.kromannreumert.client.DTO.*;
import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.client.mapper.ClientMapper;
import com.example.kromannreumert.client.repository.ClientRepository;
import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClientService {


    private final static Logger log = LoggerFactory.getLogger(ClientService.class);
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final UserRepository userRepository;
    private final LoggingService loggingService;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper, UserRepository userRepository, LoggingService loggingService) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.userRepository = userRepository;
        this.loggingService = loggingService;
    }

    public List<ClientResponeDTO> getAllClients() {
        try {
            List<Client> clients = clientRepository.findAll();
            return clients.stream().map(clientMapper::toClientDTO).toList();
        } catch (Exception e) {
            throw new RuntimeException("fuck dig");
        }

    }

    public ClientResponeDTO getClientByIdPrefix(Long idPrefix){
        Client client = clientRepository.getClientByIDPrefix(idPrefix).orElseThrow(() -> new RuntimeException("Client not found"));
        return clientMapper.toClientDTO(client);
    }

    public ClientResponeDTO getClientByName(String clientName) {
        Client client = clientRepository.findClientByName(clientName).orElseThrow(() -> new RuntimeException("Client not found"));
        return clientMapper.toClientDTO(client);
    }

    public List<String> getUserFromClient(Long idPrefix) {
        Client getClient = clientRepository.getClientByIDPrefix(idPrefix).orElseThrow(() -> new RuntimeException("Client not found"));
        return getClient.getUsers().stream().map(User::getName).toList();
    }

    public int getClientSize() {
        List<Client> getAll = clientRepository.findAll();
        return getAll.size();
    }

    public String addClient(ClientRequestDTO client) {
        try {

            Set<User> users = client.users().stream()
                    .map(username -> userRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("User not found: " + username)))
                    .collect(Collectors.toSet());

            // We do not use a mapper here, as we are ^ using a repository call
            Client createClient = new Client(null, client.clientName(), users, client.idPrefix());
            clientRepository.save(createClient);
            // add success log here
            return "Client successfully created: " + createClient.getName();
        } catch (Exception e) {
            // add fail log here
            throw new RuntimeException(e);
        }
    }


    // Single responsibility -> that is why I do not combine it with the method below
    public String updateClientName(UpdateClientNameDTO updateClient) {
        try {
            Client updatedClient = clientRepository.findClientByName(updateClient.oldName()).orElseThrow(() -> new RuntimeException("Client not found"));

            if (Objects.equals(updatedClient.getName(), updateClient.newName())) {
                return "Cannot update the same name";
            }

            updatedClient.setName(updateClient.newName());

            clientRepository.save(updatedClient);
            return "Successfully updated client with: " + updateClient.newName();
        } catch (RuntimeException e) {
            return "Could not update the client name";
        }

    }

    // Single responsibility -> that is why I do not combine it with the method below
    public String updateClientIdPrefix(UpdateClientIdPrefixDTO updateClient) {
        Client updatedClientId = clientRepository.findClientByName(updateClient.clientName()).orElseThrow(() -> new RuntimeException("Client not found"));
        if (Objects.equals(updatedClientId.getIDPrefix(), updateClient.idPrefix())) {
            return "Cannot update the same id";
        }
        updatedClientId.setIDPrefix(updateClient.idPrefix());
        clientRepository.save(updatedClientId);

        return "Successfully updated client with: " + updateClient.idPrefix();
    }

    public String updateClientUserList(UpdateClientUserList userList) {
        Client updateClientUsers = clientRepository.getClientByIDPrefix(userList.clientIdPrefix()).orElseThrow(() -> new RuntimeException("Client not found"));
        Set<User> users = userList.user().stream().map(u -> userRepository.findByName(u).orElseThrow(() -> new RuntimeException("User not found"))).collect(Collectors.toSet());
        updateClientUsers.setUsers(users);
        clientRepository.save(updateClientUsers);
        return "Successfully updated users with: " + userList.user();
    }

    public String deleteClient(Long id) {
        clientRepository.deleteById(id);
        return "Client with id: " + id + " has been deleted";
    }

}
