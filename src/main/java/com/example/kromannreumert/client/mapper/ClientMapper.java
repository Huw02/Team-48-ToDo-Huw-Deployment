package com.example.kromannreumert.client.mapper;

import com.example.kromannreumert.client.DTO.ClientResponeDTO;
import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientMapper {

    public ClientResponeDTO toClientDTO(Client client) {

        List<String> usernames = client.getUsers()
                .stream()
                .map(User::getName)
                .toList();

        return new ClientResponeDTO(
                client.getId(),
                client.getName(),
                usernames,
                client.getIDPrefix()
        );
    }
}
