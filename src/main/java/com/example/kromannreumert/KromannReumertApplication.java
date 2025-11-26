package com.example.kromannreumert;

import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.client.repository.ClientRepository;
import com.example.kromannreumert.logging.repository.LogRepository;
import com.example.kromannreumert.user.entity.Role;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.repository.RoleRepository;
import com.example.kromannreumert.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class KromannReumertApplication {

    public static void main(String[] args) {
        SpringApplication.run(KromannReumertApplication.class, args);
    }

    @Bean
    @Profile("!test")
    CommandLineRunner loadTestData(UserRepository userRepo, RoleRepository roleRepository, ClientRepository clientRepository) {
        return args -> {

            // Create Roles in DB
            Date now = new Date(2025-11-25L);
            Role admin = roleRepository.save(new Role(null,"ADMIN"));
            Role partner  = roleRepository.save(new Role(null, "PARTNER"));
            Role sagsbehandler  = roleRepository.save(new Role(null, "SAGSBEHANDLER"));
            Role jurist  = roleRepository.save(new Role(null, "JURIST"));
            User user = userRepo.save(new User(null, "bob", "bob", "bob@123.dk", "bob", now, Set.of(admin)));


            // CREATE User in DB
            userRepo.save(new User(null,"testAdmin","test","test@test.dk","test", now,Set.of(admin)));
            userRepo.save(new User(null,"testPartner","test","test@test.dk","test", now,Set.of(partner)));
            userRepo.save(new User(null,"testSagsbehandler","test","test@test.dk","test", now,Set.of(sagsbehandler)));
            userRepo.save(new User(null,"testJurist","test","test@test.dk","test", now,Set.of(jurist)));

            clientRepository.save(new Client(null, "hey", Set.of(user), 9999L));

            System.out.println("Test data indlæst i databasen");
        };
    }

}
