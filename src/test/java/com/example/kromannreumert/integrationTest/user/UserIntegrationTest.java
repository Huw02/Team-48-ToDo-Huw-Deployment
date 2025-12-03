package com.example.kromannreumert.integrationTest.user;

import com.example.kromannreumert.user.dto.UserRequestDTO;
import com.example.kromannreumert.user.dto.UserResponseDTO;
import com.example.kromannreumert.user.entity.Role;
import com.example.kromannreumert.user.repository.RoleRepository;
import com.example.kromannreumert.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

                .andExpect(jsonPath("$.size()").value(4));
    }

    @Test
    void getUserByUserId()throws Exception{
        mockMvc.perform(get(baseUrl + "/1").with(user("ADMIN").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.name").value("System Admin"))
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andExpect(jsonPath("$.createdDate").value("2024-01-01T00:00:00"))
                .andExpect(jsonPath("$.role.id").value(1))
                .andExpect(jsonPath("$.role.roleName").value("ADMIN"));
    }


    @Test
    void updateUser()throws Exception{
        LocalDateTime date = LocalDateTime.of(1, 1, 1, 1, 1);
        Role role = new Role(1L, "ADMIN");
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "testTest",
                "test",
                "test@gmail.com",
                "123",
                1);

        mockMvc.perform(put(baseUrl + "/1").with(user("ADMIN").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testTest"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.createdDate").exists())
                .andExpect(jsonPath("$.role.id").value(1))
                .andExpect(jsonPath("$.role.roleName").value("ADMIN"));
    }

    @Test
    void deleteUser()throws Exception{
        mockMvc.perform(delete(baseUrl + "/1").with(user("ADMIN").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Deleted user with user id: " + 1));
    }
    @Test
    void getUserNumber()throws Exception{
        mockMvc.perform(get(baseUrl + "/size").with(user("ADMIN").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(4));
    }



}
