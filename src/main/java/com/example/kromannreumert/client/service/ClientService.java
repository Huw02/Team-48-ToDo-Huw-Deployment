package com.example.kromannreumert.client.service;

import com.example.kromannreumert.client.DTO.CreateClientDTO;
import com.example.kromannreumert.client.DTO.UpdateClientIdPrefixDTO;
import com.example.kromannreumert.client.DTO.UpdateClientNameDTO;
import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.client.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {


    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getClientByIdPrefix(Long idPrefix){
        return clientRepository.getClientByIDPrefix(idPrefix).orElseThrow(() -> new RuntimeException("Client not found"));
    }

    public Client getClientByName(String clientName) {
        return clientRepository.findClientByName(clientName).orElseThrow(() -> new RuntimeException("Client not found"));
    }

    public String addClient(CreateClientDTO client) {
        try {
            Client createClient = new Client(null, client.clientName(), client.users(), client.idPrefix());
            clientRepository.save(createClient);
            // add success log here
            return "Client successfully created: " + createClient.getName();
        } catch (Exception e) {
            // add fail log here
            throw new RuntimeException(e);
        }
    }


    public Client updateClientName(UpdateClientNameDTO updateClient, String clientName) {
        Client updatedClient = clientRepository.findClientByName(clientName).orElseThrow(() -> new RuntimeException("Client not found"));
        updatedClient.setName(updateClient.name());
        return clientRepository.save(updatedClient);
    }

    public Client updateClientIdPrefix(String clientName, UpdateClientIdPrefixDTO updateClientIdPrefixDTO) {
        Client updatedClientId = clientRepository.findClientByName(clientName).orElseThrow(() -> new RuntimeException("Client not found"));
        updatedClientId.setIDPrefix(updateClientIdPrefixDTO.idPrefix());
        return clientRepository.save(updatedClientId);
    }

    public String deleteClient(Long id) {
        clientRepository.deleteById(id);
        return "Client with id: " + id + " has been deleted";
    }

}
