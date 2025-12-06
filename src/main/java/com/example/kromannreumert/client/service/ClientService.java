package com.example.kromannreumert.client.service;

import com.example.kromannreumert.client.DTO.*;
import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.client.mapper.ClientMapper;
import com.example.kromannreumert.client.repository.ClientRepository;
import com.example.kromannreumert.exception.customException.http4xxExceptions.ApiBusinessException;
import com.example.kromannreumert.exception.customException.http4xxExceptions.client.ClientConflictException;
import com.example.kromannreumert.exception.customException.http4xxExceptions.client.ClientNotFoundException;
import com.example.kromannreumert.exception.customException.http4xxExceptions.UserNotFoundException;
import com.example.kromannreumert.exception.customException.http5xxException.ActionFailedException;
import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final UserRepository userRepository;
    private final LoggingService loggingService;

    public ClientService(ClientRepository clientRepository,
                         ClientMapper clientMapper,
                         UserRepository userRepository,
                         LoggingService loggingService) {

        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.userRepository = userRepository;
        this.loggingService = loggingService;
    }

    public List<ClientResponeDTO> getAllClients(String actor) {
        try {
            List<Client> clients = clientRepository.findAll();
            loggingService.log(LogAction.VIEW_ALL_CLIENTS, actor, "Fetched all clients");
            return clients.stream().map(clientMapper::toClientDTO).toList();

        } catch (Exception e) {
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.VIEW_ALL_CLIENTS_FAILED, actor, e);
        }
    }

    public ClientResponeDTO getClientByIdPrefix(Long idPrefix, String actor) {
        try {
            Client client = clientRepository.getClientByIDPrefix(idPrefix)
                    .orElseThrow(() ->
                            new ClientNotFoundException(
                                    LogAction.VIEW_ONE_CLIENT_FAILED,
                                    actor,
                                    "idPrefix=" + idPrefix
                            ));

            loggingService.log(LogAction.VIEW_ONE_CLIENT, actor,
                    "Viewed client with prefix: " + idPrefix);

            return clientMapper.toClientDTO(client);

        } catch (Exception e) {
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.VIEW_ONE_CLIENT_FAILED, actor, e);
        }
    }


    public ClientResponeDTO getClientByName(String clientName, String actor) {
        try {
            Client client = clientRepository.findClientByName(clientName)
                    .orElseThrow(() ->
                            new ClientNotFoundException(
                                    LogAction.VIEW_ONE_CLIENT_FAILED,
                                    actor,
                                    "name='" + clientName + "'"
                            ));

            loggingService.log(LogAction.VIEW_ONE_CLIENT, actor,
                    "Viewed client with name: " + clientName);

            return clientMapper.toClientDTO(client);

        } catch (Exception e) {
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.VIEW_ONE_CLIENT_FAILED, actor, e);
        }
    }


    public List<String> getUserFromClient(Long idPrefix, String actor) {
        try {
            Client client = clientRepository.getClientByIDPrefix(idPrefix)
                    .orElseThrow(() ->
                            new ClientNotFoundException(
                                    LogAction.VIEW_ONE_CLIENT_FAILED,
                                    actor,
                                    "idPrefix=" + idPrefix
                            ));

            loggingService.log(LogAction.VIEW_ONE_CLIENT, actor,
                    "Viewed users for client prefix: " + idPrefix);

            return client.getUsers().stream().map(User::getName).toList();

        } catch (Exception e) {
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.VIEW_ALL_USERS_FAILED, actor, e);
        }
    }


    public long getClientSize(String actor) {
        try {
            long count = clientRepository.count();

            loggingService.log(LogAction.VIEW_ALL_CLIENTS, actor, "Checked client count");

            return count;

        } catch (Exception e) {
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.VIEW_ALL_CLIENTS_FAILED, actor, e);
        }
    }

    public String addClient(ClientRequestDTO clientDTO, String actor) {
        try {
            Set<User> users = clientDTO.users().stream()
                    .map(username -> userRepository.findByUsername(username)
                            .orElseThrow(() ->
                                    new UserNotFoundException(
                                            LogAction.VIEW_ONE_USER_FAILED,
                                            actor,
                                            "username='" + username + "'"
                                    )))
                    .collect(Collectors.toSet());

            Client client = new Client(null, clientDTO.clientName(), users, clientDTO.idPrefix());
            clientRepository.save(client);

            loggingService.log(LogAction.CREATE_CLIENT, actor,
                    "Created client: " + client.getName());

            return "Client successfully created: " + client.getName();

        } catch (Exception e) {
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.CREATE_CLIENT_FAILED, actor, e);
        }
    }

    public String updateClientName(UpdateClientNameDTO dto, String actor) {
        try {
            Client client = clientRepository.findClientByName(dto.oldName())
                    .orElseThrow(() ->
                            new ClientNotFoundException(
                                    LogAction.UPDATE_CLIENT_FAILED,
                                    actor,
                                    "name='" + dto.oldName() + "'"
                            ));

            if (client.getName().equals(dto.newName())) {
                throw new ClientConflictException(
                        LogAction.UPDATE_CLIENT_FAILED, actor,
                        "Attempted to update name with same value"
                );
            }

            client.setName(dto.newName());
            clientRepository.save(client);

            loggingService.log(LogAction.UPDATE_CLIENT, actor,
                    "Updated client name to: " + dto.newName());

            return "Successfully updated client with: " + dto.newName();

        } catch (Exception e) {
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.UPDATE_CLIENT_FAILED, actor, e);
        }
    }

    public String updateClientIdPrefix(UpdateClientIdPrefixDTO dto, String actor) {
        try {
            Client client = clientRepository.findClientByName(dto.clientName())
                    .orElseThrow(() ->
                            new ClientNotFoundException(
                                    LogAction.UPDATE_CLIENT_FAILED,
                                    actor,
                                    "name='" + dto.clientName() + "'"
                            ));

            if (client.getIDPrefix().equals(dto.idPrefix())) {
                throw new ClientConflictException(
                        LogAction.UPDATE_CLIENT_FAILED, actor,
                        "Attempted to update name with same value"
                );
            }

            client.setIDPrefix(dto.idPrefix());
            clientRepository.save(client);

            loggingService.log(LogAction.UPDATE_CLIENT, actor,
                    "Updated client prefix");

            return "Successfully updated client with: " + dto.idPrefix();

        } catch (Exception e) {
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.UPDATE_CLIENT_FAILED, actor, e);
        }
    }


    public String updateClientUserList(UpdateClientUserList dto, String actor) {
        try {
            Client client = clientRepository.getClientByIDPrefix(dto.clientIdPrefix())
                    .orElseThrow(() ->
                            new ClientNotFoundException(
                                    LogAction.UPDATE_CLIENT_FAILED,
                                    actor,
                                    "idPrefix: " + dto.clientIdPrefix()
                            ));

            Set<User> users = dto.user().stream()
                    .map(name -> userRepository.findByName(name)
                            .orElseThrow(() ->
                                    new UserNotFoundException(
                                            LogAction.VIEW_ONE_USER_FAILED,
                                            actor,
                                            "name: " + name
                                    )))
                    .collect(Collectors.toSet());

            client.setUsers(users);
            clientRepository.save(client);

            loggingService.log(LogAction.UPDATE_CLIENT, actor,
                    "Updated client users");

            return "Client users updated";

        } catch (Exception e) {
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.UPDATE_CLIENT_FAILED, actor, e);
        }
    }

    public String deleteClient(Long id, String actor) {
        try {

            if(clientRepository.findById(id).isEmpty()) {
                throw new ClientNotFoundException(
                        LogAction.DELETE_CLIENT_FAILED,
                        actor,
                        "Not able to delete client with id: " + id + " as it does not exist");
            };

            clientRepository.deleteById(id);

            loggingService.log(LogAction.DELETE_CLIENT, actor,
                    "Deleted client id=" + id);

            return "Client with id: " + id + " has been deleted";

        } catch (Exception e) {
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.DELETE_CLIENT_FAILED, actor, e);
        }
    }
}

