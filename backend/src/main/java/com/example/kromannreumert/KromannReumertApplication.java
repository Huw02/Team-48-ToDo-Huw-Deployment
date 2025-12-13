package com.example.kromannreumert;

import com.example.kromannreumert.casee.entity.Casee;
import com.example.kromannreumert.casee.repository.CaseRepository;
import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.client.repository.ClientRepository;
import com.example.kromannreumert.logging.repository.LogRepository;
import com.example.kromannreumert.todo.entity.Priority;
import com.example.kromannreumert.todo.entity.Status;
import com.example.kromannreumert.todo.entity.ToDo;
import com.example.kromannreumert.todo.repository.ToDoRepository;
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
import java.time.LocalDate;
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

    // test data
    @Bean
    @Profile("!test")
    CommandLineRunner loadTestData(UserRepository userRepo, RoleRepository roleRepository, ClientRepository clientRepository, CaseRepository caseRepository, ToDoRepository toDoRepository) {
        return args -> {

            // Create Roles in DB
            LocalDateTime now = LocalDateTime.now();
            Role admin = roleRepository.save(new Role(null,"ADMIN"));
            Role partner  = roleRepository.save(new Role(null, "PARTNER"));
            Role sagsbehandler  = roleRepository.save(new Role(null, "SAGSBEHANDLER"));
            Role jurist  = roleRepository.save(new Role(null, "JURIST"));

            User adminJacob = userRepo.save(new User(
                    null, "Jacob", "Jacob", "bob@123.dk", "bob", now, Set.of(admin)
            ));

            // Testbrugere – GEM referencer
            User testAdmin = userRepo.save(new User(
                    null,"testAdmin","Simon","test@test.dk", passwordEncoder.encode("test"), now, Set.of(admin)
            ));

            User testPartner = userRepo.save(new User(
                    null,"testPartner","Hannibal","test@test.dk", passwordEncoder.encode("test"), now, Set.of(partner)
            ));

            User testSagsbehandler = userRepo.save(new User(
                    null,"testSagsbehandler","Jesus","test@test.dk", passwordEncoder.encode("test"), now, Set.of(sagsbehandler)
            ));

            User testJurist = userRepo.save(new User(
                    null,"testJurist","Victor","test@test.dk", passwordEncoder.encode("test"), now, Set.of(jurist)
            ));

            Client client1 = clientRepository.save(new Client(null, "Zahaa Enterprise", Set.of(testPartner), 99000L));
            Client client2 = clientRepository.save(new Client(null, "Hannibal Enterprise", Set.of(testPartner), 99001L));
            Client client3 = clientRepository.save(new Client(null, "Victor Enterprise", Set.of(testPartner), 99002L));
            Client client4 = clientRepository.save(new Client(null, "MonneDev Enterprise", Set.of(testPartner), 99003L));
            Client client5 = clientRepository.save(new Client(null, "Kromann", Set.of(testPartner), 99004L));
            Client client6 = clientRepository.save(new Client(null, "hey", Set.of(testAdmin), 9999L));


            Client caseClient = clientRepository.findById(Long.valueOf(1))
                    .orElseThrow(() -> new EntityNotFoundException("Client not found"));
            Set<User> caseUsers = new HashSet<>();
            caseUsers.add(userRepo.findById(5)
                    .orElseThrow(() -> new RuntimeException("User not found")));

            Set<User> onlySagsbehandler = Set.of(testSagsbehandler);
            Set<User> sagsOgJurist = Set.of(testSagsbehandler, testJurist);

            Casee case1 = caseRepository.save(new Casee(
                    "Ejendomshandel – Zahaa",
                    client1,
                    onlySagsbehandler,
                    10455L,
                    testPartner,
                    LocalDateTime.now()
            ));

            Casee case2 = caseRepository.save(new Casee(
                    "Kontraktgennemgang – Hannibal",
                    client2,
                    onlySagsbehandler,
                    10696L,
                    testPartner,
                    LocalDateTime.now()
            ));

            // Case med både sagsbehandler OG jurist
            Casee caseMedJurist = caseRepository.save(new Casee(
                    "Tvist – Kontraktsbrud",
                    client3,
                    sagsOgJurist,
                    10777L,
                    testPartner,
                    LocalDateTime.now()
            ));

            // TODOS under caseMedJurist
            ToDo todo1 = new ToDo();
            todo1.setName("Gennemgå kontrakt og bilag");
            todo1.setDescription("Jurist gennemgår hovedkontrakt og vedhæftede bilag for risici.");
            todo1.setCaseId(caseMedJurist);
            todo1.setCreated(now);
            todo1.setStartDate(LocalDate.now());
            todo1.setEndDate(LocalDate.now().plusDays(3));
            // todo1.setPriority(Priority.HIGH);
            // todo1.setStatus(Status.IN_PROGRESS);
            todo1.setArchived(false);
            // Jurist ER assignet på denne
            todo1.setUsers(Set.of(testJurist));
            todo1.setStatus(Status.NOT_STARTED);
            todo1.setPriority(Priority.MEDIUM);

            ToDo todo2 = new ToDo();
            todo2.setName("Indhent supplerende materiale fra klient");
            todo2.setDescription("Sagsbehandler kontakter klient for manglende dokumentation.");
            todo2.setCaseId(caseMedJurist);
            todo2.setCreated(now);
            todo2.setStartDate(LocalDate.now());
            todo2.setEndDate(LocalDate.now().plusDays(5));
            // todo2.setPriority(Priority.MEDIUM);
            // todo2.setStatus(Status.TODO);
            todo2.setArchived(false);
            // KUN sagsbehandler på denne
            todo2.setUsers(Set.of(testSagsbehandler));
            todo2.setStatus(Status.NOT_STARTED);
            todo2.setPriority(Priority.MEDIUM);

            ToDo todo3 = new ToDo();
            todo3.setName("Forbered udkast til processkrift");
            todo3.setDescription("Jurist udarbejder første udkast til processkrift baseret på fakta.");
            todo3.setCaseId(caseMedJurist);
            todo3.setCreated(now);
            todo3.setStartDate(LocalDate.now().plusDays(1));
            todo3.setEndDate(LocalDate.now().plusDays(7));
            // todo3.setPriority(Priority.HIGH);
            // todo3.setStatus(Status.TODO);
            todo3.setArchived(false);
            // Her er både sagsbehandler og jurist assignet
            todo3.setUsers(Set.of(testSagsbehandler, testJurist));
            todo3.setStatus(Status.NOT_STARTED);
            todo3.setPriority(Priority.MEDIUM);

            toDoRepository.saveAll(List.of(todo1, todo2, todo3));


            System.out.println("Test data indlæst i databasen");
        };
    }

}
