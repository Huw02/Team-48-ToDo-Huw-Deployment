package com.example.kromannreumert.client.service;

import com.example.kromannreumert.client.DTO.*;
import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.client.mapper.ClientMapper;
import com.example.kromannreumert.client.repository.ClientRepository;
import com.example.kromannreumert.exception.customException.ClientNotFoundException;
import com.example.kromannreumert.logging.entity.LogAction;
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

    public List<ClientResponeDTO> getAllClients(String name) {
        try {
            List<Client> clients = clientRepository.findAll();
            loggingService.log(LogAction.VIEW_ALL_CLIENTS, name, "has fetched all clients successfully");
            return clients.stream().map(clientMapper::toClientDTO).toList();
        } catch (Exception e) {
            loggingService.log(LogAction.VIEW_ALL_CLIENTS_FAILED, name, "tried to fetch all clients but failed");
            throw new RuntimeException("Could not fetch all clients");
        }

    }

    public ClientResponeDTO getClientByIdPrefix(Long idPrefix, String name){
        try {
            Client client = clientRepository.getClientByIDPrefix(idPrefix).orElseThrow(() -> new ClientNotFoundException(idPrefix));

            loggingService.log(LogAction.VIEW_ONE_CLIENT, name, "Viewed client with id prefix: " + idPrefix);
            return clientMapper.toClientDTO(client);
        } catch (RuntimeException e) {
            loggingService.log(LogAction.VIEW_ONE_CLIENT_FAILED, name, "Failed to view client with id prefix: " + idPrefix);
            throw new RuntimeException("Client not found", e);
        }
    }

    public ClientResponeDTO getClientByName(String clientName, String name) {
        try {
            Client client = clientRepository.findClientByName(clientName).orElseThrow(() -> new RuntimeException("Client not found"));

            loggingService.log(LogAction.VIEW_ONE_CLIENT, name, "Viewed client with name: " + clientName);

            return clientMapper.toClientDTO(client);
        } catch (RuntimeException e) {
            loggingService.log(LogAction.VIEW_ONE_CLIENT_FAILED, name, "Failed to view client with name: " + clientName);
            throw new RuntimeException("Client not found", e);
        }
    }

    public List<String> getUserFromClient(Long idPrefix, String name) {
        try {
            Client getClient = clientRepository.getClientByIDPrefix(idPrefix).orElseThrow(() -> new RuntimeException("Client not found"));

            loggingService.log(LogAction.VIEW_ONE_CLIENT, name, "Viewed client users for id prefix: " + idPrefix);

            return getClient.getUsers().stream().map(User::getName).toList();
        } catch (RuntimeException e) {
            loggingService.log(LogAction.VIEW_ONE_CLIENT_FAILED, name, "Failed to view client users for id prefix: " + idPrefix);
            throw new RuntimeException("Client not found", e);
        }
    }

    public int getClientSize(String name) {
        try {
            List<Client> getAll = clientRepository.findAll();

            loggingService.log(LogAction.VIEW_ALL_CLIENTS, name, "Checked client size");

            return getAll.size();
        } catch (RuntimeException e) {
            loggingService.log(LogAction.VIEW_ALL_CLIENTS_FAILED, name, "Failed to check client size");
            throw new RuntimeException("Failed to retrieve client size", e);
        }
    }

    public String addClient(ClientRequestDTO client, String name) {
        try {

            Set<User> users = client.users().stream()
                    .map(username -> userRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("User not found: " + username)))
                    .collect(Collectors.toSet());

            // We do not use a mapper here, as we are ^ using a repository call
            Client createClient = new Client(null, client.clientName(), users, client.idPrefix());
            clientRepository.save(createClient);

            loggingService.log(LogAction.CREATE_CLIENT, name, "Created client: " + createClient.getName());

            return "Client successfully created: " + createClient.getName();
        } catch (Exception e) {
            loggingService.log(LogAction.CREATE_CLIENT_FAILED, name, "Failed to create client: " + client.clientName());
            throw new RuntimeException("Could not create client: " + client.clientName(), e);
        }
    }


    // Single responsibility -> that is why I do not combine it with the method below
    public String updateClientName(UpdateClientNameDTO updateClient, String name) {
        try {
            Client updatedClient = clientRepository.findClientByName(updateClient.oldName()).orElseThrow(() -> new RuntimeException("Client not found"));

            if (Objects.equals(updatedClient.getName(), updateClient.newName())) {
                loggingService.log(LogAction.UPDATE_CLIENT_FAILED, name, "Attempted to update client with the same name: " + updateClient.newName());
                return "Cannot update the same name";
            }

            updatedClient.setName(updateClient.newName());

            clientRepository.save(updatedClient);

            loggingService.log(LogAction.UPDATE_CLIENT, name, "Updated client name to: " + updateClient.newName() + " from: " + updateClient.oldName());
            return "Successfully updated client with: " + updateClient.newName();
        } catch (RuntimeException e) {
            loggingService.log(LogAction.UPDATE_CLIENT_FAILED, name, "Failed to update client name for: " + updateClient.oldName());
            throw new RuntimeException("Could not update the client name", e);
        }

    }

    // Single responsibility -> that is why I do not combine it with the method below
    public String updateClientIdPrefix(UpdateClientIdPrefixDTO updateClient, String name) {
        try {
            Client updatedClientId = clientRepository.findClientByName(updateClient.clientName()).orElseThrow(() -> new RuntimeException("Client not found"));
            if (Objects.equals(updatedClientId.getIDPrefix(), updateClient.idPrefix())) {
                loggingService.log(LogAction.UPDATE_CLIENT_FAILED, name, "Attempted to update client id prefix with the same value for: " + updateClient.clientName());
                return "Cannot update the same id";
            }
            updatedClientId.setIDPrefix(updateClient.idPrefix());
            clientRepository.save(updatedClientId);

            loggingService.log(LogAction.UPDATE_CLIENT, name, "Updated client id prefix for: " + updateClient.clientName());

            return "Successfully updated client with: " + updateClient.idPrefix();
        } catch (RuntimeException e) {
            loggingService.log(LogAction.UPDATE_CLIENT_FAILED, name, "Failed to update client id prefix for: " + updateClient.clientName());
            throw new RuntimeException("Could not update the client id prefix", e);
        }
    }

    public String updateClientUserList(UpdateClientUserList userList, String name) {
        try {
            Client updateClientUsers = clientRepository.getClientByIDPrefix(userList.clientIdPrefix()).orElseThrow(() -> new RuntimeException("Client not found"));
            Set<User> users = userList.user().stream().map(u -> userRepository.findByName(u).orElseThrow(() -> new RuntimeException("User not found"))).collect(Collectors.toSet());
            updateClientUsers.setUsers(users);
            clientRepository.save(updateClientUsers);

            loggingService.log(LogAction.UPDATE_CLIENT, name, "Updated client users for id prefix: " + userList.clientIdPrefix());
            return "Successfully updated users with: " + userList.user();
        } catch (RuntimeException e) {
            loggingService.log(LogAction.UPDATE_CLIENT_FAILED, name, "Failed to update client users for id prefix: " + userList.clientIdPrefix());
            throw new RuntimeException("Could not update client user list", e);
        }
    }

    public String deleteClient(Long id, String name) {
        try {
            clientRepository.deleteById(id);
            loggingService.log(LogAction.DELETE_CLIENT, name, "Deleted client with id: " + id);
            return "Client with id: " + id + " has been deleted";
        } catch (RuntimeException e) {
            loggingService.log(LogAction.DELETE_CLIENT_FAILED, name, "Failed to delete client with id: " + id);
            throw new RuntimeException("Could not delete client with id: " + id, e);
        }
    }

}
