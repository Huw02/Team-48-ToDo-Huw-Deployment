package com.example.kromannreumert.unitTest.user;

import com.example.kromannreumert.security.config.SecurityConfig;
import com.example.kromannreumert.user.controller.UserController;
import com.example.kromannreumert.user.dto.UserRequestDTO;
import com.example.kromannreumert.user.dto.UserResponseDTO;
import com.example.kromannreumert.user.entity.Role;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.service.UserService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class UserUnitTestController {


    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    private final String baseURL = "/api/v1/user";


    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void getAllUsersWhileLoggedIn() throws Exception{
        LocalDateTime date = LocalDateTime.of(1, 1, 1, 1, 1);
        Role role = new Role(1L, "ADMIN");

        List<UserResponseDTO>users = Arrays.asList(new UserResponseDTO(1, "testTest", "test", "test@gmail.com", date, role), new UserResponseDTO(2, "testTest2", "test2", "test@gmail.com2", date, role));
        when(userService.getAllUsers("testUser")).thenReturn(users);

        mockMvc.perform(get(baseURL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].userId").value(1))
                .andExpect(jsonPath("$[0].username").value("testTest"))
                .andExpect(jsonPath("$[0].name").value("test"))
                .andExpect(jsonPath("$[0].email").value("test@gmail.com"))
                .andExpect(jsonPath("$[0].createdDate").value("0001-01-01T01:01:00"))
                .andExpect(jsonPath("$[0].role.id").value(1))
                .andExpect(jsonPath("$[0].role.roleName").value("ADMIN")
                                                        );

    }


    @Test
    void getAllUsersNotLoggedIn() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    //de tre nedestående test, de tester at auth virker for user, da man får 403(forbidden) i stedet for 401(unAuthorized), som ovenover
    @Test
    @WithMockUser(roles = "PARTNER")
    void getAllUsersWhileLoggedInAsPartner() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
    @Test
    @WithMockUser(roles = "SAGSBEHANDLER")
    void getAllUsersWhileLoggedInAsSagsbehandler() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
    @Test
    @WithMockUser(roles = "JURIST")
    void getAllUsersWhileLoggedInAsJurist() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }



    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void getUserByIdWhileLoggedIn() throws Exception{
        LocalDateTime date = LocalDateTime.of(1, 1, 1, 1, 1);
        Role role = new Role(1L, "ADMIN");

        UserResponseDTO user = new UserResponseDTO(1, "testTest", "test", "test@gmail.com", date, role);
        when(userService.getUserByUserId(1, "testUser")).thenReturn(user);

        mockMvc.perform(get(baseURL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testTest"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.createdDate").value("0001-01-01T01:01:00"))
                .andExpect(jsonPath("$.role.id").value(1))
                .andExpect(jsonPath("$.role.roleName").value("ADMIN")
                );
    }


    @Test
    void getUserByIdNotLoggedIn() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    //sætter username=huw, så jeg kan mocke principal metoden, der sender den loggede ind persons navn med til logging
    @Test
    @WithMockUser(username ="testUser",  roles = "ADMIN")
    void updateUserdWhileLoggedIn() throws Exception{
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "huw",
                "hannibal",
                "huw@gmail.com",
                "123",
                1);
        LocalDateTime date = LocalDateTime.of(1, 1, 1, 1, 1);
        Role role = new Role(1L, "ADMIN");

        UserResponseDTO user = new UserResponseDTO(1, "testTest", "test", "test@gmail.com", date, role);
        when(userService.getUserByUserId(1, "testUser")).thenReturn(user);


        //blev nødt til at bruge eq, da den ellers brokkede sig over, at der stod en "raw" string, er ikke helt sikker på hvorfor
        when(userService.getUserByUserId(eq(1), eq("testUser"))).thenReturn(user);

        when(userService.updateUser(eq(1), any(UserRequestDTO.class), eq("testUser"))).thenReturn(user);

        mockMvc.perform(put(baseURL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testTest"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.createdDate").value("0001-01-01T01:01:00"))
                .andExpect(jsonPath("$.role.id").value(1))
                .andExpect(jsonPath("$.role.roleName").value("ADMIN")
                );
    }


    @Test
    void updateUserNotLoggedIn() throws Exception{
        mockMvc.perform(put("/api/v1/user/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }


    @Test
    @WithMockUser(username ="testUser",  roles = "ADMIN")
    void deleteUserdWhileLoggedIn() throws Exception{

        LocalDateTime createdDate = LocalDateTime.now();

        Role role = new Role(1L, "ADMIN" );

        UserResponseDTO mockResponse = new UserResponseDTO(
                1, "huw", "hanni", "gmail", createdDate, role
        );

        //blev nødt til at bruge eq, da den ellers brokkede sig over, at der stod en "raw" string, er ikke helt sikker på hvorfor
        when(userService.getUserByUserId(eq(1), eq("testUser"))).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(delete(baseURL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Deleted user with user id: " + 1));
    }


    @Test
    void deleteUserNotLoggedIn() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }









}
