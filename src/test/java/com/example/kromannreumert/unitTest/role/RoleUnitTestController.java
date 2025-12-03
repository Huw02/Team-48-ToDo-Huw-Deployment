package com.example.kromannreumert.unitTest.role;

import com.example.kromannreumert.security.config.SecurityConfig;
import com.example.kromannreumert.user.controller.RoleController;
import com.example.kromannreumert.user.dto.RoleRequestDTO;
import com.example.kromannreumert.user.dto.RoleResponseDTO;
import com.example.kromannreumert.user.entity.Role;
import com.example.kromannreumert.user.service.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RoleController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class RoleUnitTestController {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RoleService roleService;

    @Autowired
    ObjectMapper objectMapper;

    private final String baseURL = "/api/v1/role";

    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void getAllRolesWhileLoggedIn() throws Exception{
        List<RoleResponseDTO>roles = Arrays.asList(new RoleResponseDTO(1L, "ADMIN"), new RoleResponseDTO(2L, "Partner"));
        when(roleService.getAllRoles("testUser")).thenReturn(roles);


        mockMvc.perform(get(baseURL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$[0].roleName").value("ADMIN"));
    }

    @Test
    void getAllRolesNotLoggedIn() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/role"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    //De tre nedenstående test bekræfter at role auth virker, fordi den giver en 403 i stedet for 401, som ovenover
    @Test
    @WithMockUser(roles = "PARTNER")
    void getAllRolesWhileLoggedInAsPartner() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/role"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
    @Test
    @WithMockUser(roles = "SAGSBEHANDLER")
    void getAllRolesWhileLoggedInAsSagsbehandler() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/role"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
    @Test
    @WithMockUser(roles = "Jurist")
    void getAllRolesWhileLoggedInAsJurist() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/role"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }


    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void getSpecifikRoleWhileLoggedIn() throws Exception{

        RoleResponseDTO role = new RoleResponseDTO(1L, "ADMIN");
        when(roleService.getRolebyRoleId(1, "testUser")).thenReturn(role);


        mockMvc.perform(get(baseURL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.roleName").value("ADMIN"));


        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/role/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getSpecifikRoleNotLoggedIn() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/role/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }


    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void createRoleWhileLoggedIn() throws Exception{
        RoleResponseDTO response = new RoleResponseDTO(1L, "ADMIN");
        RoleRequestDTO request = new RoleRequestDTO("ADMIN");


        when(roleService.createRole(any(RoleRequestDTO.class), eq("testUser"))).thenReturn(response);

        mockMvc.perform(post(baseURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.roleName").value("ADMIN"));

    }

    @Test
    void createRoleNotLoggedIn() throws Exception{
        RoleRequestDTO request = new RoleRequestDTO("ADMIN");
        RoleResponseDTO response = new RoleResponseDTO(1L, "ADMIN");

        when(roleService.createRole(any(RoleRequestDTO.class), eq("huw"))).thenReturn(response);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void updateRoleWhileLoggedIn() throws Exception{
        RoleRequestDTO request = new RoleRequestDTO("ADMIN");
        RoleResponseDTO response = new RoleResponseDTO(1L, "ADMIN");

        when(roleService.getRolebyRoleId(eq(1), eq("testUser"))).thenReturn(response);
        when(roleService.updateRole(eq(1), any(RoleRequestDTO.class), eq("testUser"))).thenReturn(response);

        mockMvc.perform(put(baseURL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.roleName").value("ADMIN"));

    }

    @Test
    void updateRoleNotLoggedIn() throws Exception{
        RoleRequestDTO request = new RoleRequestDTO("ADMIN");
        RoleResponseDTO response = new RoleResponseDTO(1L, "ADMIN");

        when(roleService.updateRole(eq(1), any(RoleRequestDTO.class), eq("huw"))).thenReturn(response);


        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/role/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "huw", roles = "ADMIN")
    void deleteRoleWhileLoggedIn() throws Exception{
        RoleResponseDTO response = new RoleResponseDTO(1L, "ADMIN");

        when(roleService.getRolebyRoleId(eq(1), eq("huw"))).thenReturn(response);

        mockMvc.perform(delete(baseURL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("The role was deleted"));

    }

    @Test
    void deleteRoleNotLoggedIn() throws Exception{
        RoleResponseDTO response = new RoleResponseDTO(1L, "ADMIN");

        when(roleService.getRolebyRoleId(eq(1), eq("huw"))).thenReturn(response);


        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/role/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
