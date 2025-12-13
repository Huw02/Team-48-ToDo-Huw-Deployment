package com.example.kromannreumert.integrationTest.role;

import com.example.kromannreumert.user.dto.RoleRequestDTO;
import com.example.kromannreumert.user.entity.Role;

import com.example.kromannreumert.user.repository.RoleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class RoleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ApplicationContext context;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ObjectMapper objectMapper;

    private final String baseUrl = "/api/v1/role";


    @Test
    void getAllRoles() throws Exception{
        mockMvc.perform(get(baseUrl).with(user("ADMIN").roles("ADMIN")))
                .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].roleName").value("ADMIN"))
                .andExpect(jsonPath("$[1].id").value(4))
                .andExpect(jsonPath("$[1].roleName").value("JURIST"))
                .andExpect(jsonPath("$[2].id").value(2))
                .andExpect(jsonPath("$[2].roleName").value("PARTNER"))
                .andExpect(jsonPath("$[3].id").value(3))
                .andExpect(jsonPath("$[3].roleName").value("SAGSBEHANDLER"));
    }

    @Test
    void getSpecifikRole() throws Exception{
        mockMvc.perform(get(baseUrl + "/1").with(user("ADMIN").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.roleName").value("ADMIN"));
    }

    @Test
    void createRole()throws Exception{
        RoleRequestDTO request = new RoleRequestDTO("TEST");

        mockMvc.perform(post(baseUrl).with(user("ADMIN").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5)) //der er allerde 4 andre roller i systemet
                .andExpect(jsonPath("$.roleName").value("TEST"));
    }

    @Test
    void updateRole()throws Exception{
        RoleRequestDTO request = new RoleRequestDTO("TEST");

        mockMvc.perform(put(baseUrl + "/1").with(user("ADMIN").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1)) //der er allerde 4 andre roller i systemet
                .andExpect(jsonPath("$.roleName").value("TEST"));
    }

    @Test
    void deleteRole()throws Exception{
        RoleRequestDTO request = new RoleRequestDTO("TEST");
        Optional<Role>beforeDelete = roleRepository.findById(1);
        assertTrue(beforeDelete.isPresent());

        mockMvc.perform(delete(baseUrl + "/1").with(user("ADMIN").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("The role was deleted"));

        Optional<Role>afterDelete = roleRepository.findById(1);
        assertTrue(afterDelete.isEmpty());
    }






}
