package com.example.kromannreumert.client.repository;

import com.example.kromannreumert.client.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> getClientByIDPrefix(Long idPrefix);

    Optional<Client> findClientByName(String name);

    void deleteByIDPrefix(Long id);
}
