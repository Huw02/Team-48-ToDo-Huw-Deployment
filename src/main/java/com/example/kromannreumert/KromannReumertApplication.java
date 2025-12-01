package com.example.kromannreumert;

import com.example.kromannreumert.casee.entity.Casee;
import com.example.kromannreumert.casee.repository.CaseRepository;
import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.client.repository.ClientRepository;
import com.example.kromannreumert.logging.repository.LogRepository;
import com.example.kromannreumert.user.entity.Role;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.repository.RoleRepository;
import com.example.kromannreumert.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class KromannReumertApplication {

    public KromannReumertApplication(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(KromannReumertApplication.class, args);
    }

    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile("!test")
    CommandLineRunner loadTestData(UserRepository userRepo, RoleRepository roleRepository, ClientRepository clientRepository, CaseRepository caseRepository) {
        return args -> {

            // Create Roles in DB
            LocalDateTime now = LocalDateTime.now();
            Role admin = roleRepository.save(new Role(null,"ADMIN"));
            Role partner  = roleRepository.save(new Role(null, "PARTNER"));
            Role sagsbehandler  = roleRepository.save(new Role(null, "SAGSBEHANDLER"));
            Role jurist  = roleRepository.save(new Role(null, "JURIST"));
            User user = userRepo.save(new User(null, "Jacob", "Jacob", "bob@123.dk", "bob", now, Set.of(admin)));


            // CREATE User in DB
            userRepo.save(new User(null,"testAdmin","Simon","test@test.dk", passwordEncoder.encode( "test"), now,Set.of(admin)));
            userRepo.save(new User(null,"testPartner","Hannibal","test@test.dk",passwordEncoder.encode("test"), now,Set.of(partner)));
            userRepo.save(new User(null,"testSagsbehandler","Jesus","test@test.dk",passwordEncoder.encode("test"), now,Set.of(sagsbehandler)));
            userRepo.save(new User(null,"testJurist","Victor","test@test.dk",passwordEncoder.encode("test"), now,Set.of(jurist)));

            clientRepository.save(new Client(null, "Zahaa Enterprise", Set.of(user), 99000L));
            clientRepository.save(new Client(null, "Hannibal Enterprise", Set.of(user), 99001L));
            clientRepository.save(new Client(null, "Victor Enterprise", Set.of(user), 99002L));
            clientRepository.save(new Client(null, "MonneDev Enterprise", Set.of(user), 99003L));
            clientRepository.save(new Client(null, "Kromann", Set.of(user), 99004L));


                clientRepository.save(new Client(null, "hey", Set.of(user), 9999L));


                Client caseClient = clientRepository.findById(Long.valueOf(1))
                                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
                Set<User> caseUsers = new HashSet<>();
                caseRepository.save(new Casee("Ossas-Sagen", caseClient, caseUsers, 10455L));



            System.out.println("Test data indlæst i databasen");
        };
    }

}
