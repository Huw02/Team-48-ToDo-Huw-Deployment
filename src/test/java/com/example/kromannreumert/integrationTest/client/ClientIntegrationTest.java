package com.example.kromannreumert.integrationTest.client;

import com.example.kromannreumert.client.repository.ClientRepository;
import com.example.kromannreumert.client.service.ClientService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ClientIntegrationTest {


    @Autowired
    ClientRepository cilentRepo;

    @Autowired
    ClientService clientService;

    @Autowired
    ApplicationContext context;

    @Test
    void context() {
        assertNotNull(context);
    }


    @Test
    void getAll() {
        //List<Client> getAll = clientService.getAllClients();
       // assertFalse(getAll.isEmpty());

    }
}
