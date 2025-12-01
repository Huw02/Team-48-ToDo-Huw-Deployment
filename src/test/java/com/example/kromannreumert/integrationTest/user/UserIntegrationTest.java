package com.example.kromannreumert.integrationTest.user;

import com.example.kromannreumert.user.repository.RoleRepository;
import com.example.kromannreumert.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ApplicationContext context;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    private final String baseUrl = "/api/v1/user";

    @Test
    void getAllRoles() throws Exception{
        mockMvc.perform(get(baseUrl).with(user("ADMIN").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].userId").value(1))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].name").value("System Admin"))
                .andExpect(jsonPath("$[0].email").value("admin@example.com"))
                .andExpect(jsonPath("$[0].createdDate").value("2024-01-01T00:00:00"))
                .andExpect(jsonPath("$[0].role.id").value(1))
                .andExpect(jsonPath("$[0].role.roleName").value("ADMIN"))

                .andExpect(jsonPath("$.size()").value(3));
    }

    @Test
    void getUserByUserId()throws Exception{

    }


}
