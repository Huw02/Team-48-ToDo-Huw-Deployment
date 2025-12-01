package com.example.kromannreumert.unitTest.user;

import com.example.kromannreumert.security.config.SecurityConfig;
import com.example.kromannreumert.user.controller.UserController;
import com.example.kromannreumert.user.dto.UserRequestDTO;
import com.example.kromannreumert.user.dto.UserResponseDTO;
import com.example.kromannreumert.user.entity.Role;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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


    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsersWhileLoggedIn() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user"))
                .andExpect(MockMvcResultMatchers.status().isOk());
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
    @WithMockUser(roles = "ADMIN")
    void getUserByIdWhileLoggedIn() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    void getUserByIdNotLoggedIn() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    //sætter username=huw, så jeg kan mocke principal metoden, der sender den loggede ind persons navn med til logging
    @Test
    @WithMockUser(username ="huw",  roles = "ADMIN")
    void updateUserdWhileLoggedIn() throws Exception{
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "huw",
                "hannibal",
                "huw@gmail.com",
                "123",
                1);

        LocalDateTime createdDate = LocalDateTime.now();
        Role role = new Role(1L, "ADMIN" );
        UserResponseDTO mockResponse = new UserResponseDTO(
                1, "huw", "hanni", "gmail", createdDate, role
        );
        //blev nødt til at bruge eq, da den ellers brokkede sig over, at der stod en "raw" string, er ikke helt sikker på hvorfor
        when(userService.getUserByUserId(eq(1), eq("huw"))).thenReturn(mockResponse);

        when(userService.updateUser(eq(1), any(UserRequestDTO.class), eq("huw"))).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    void updateUserNotLoggedIn() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }


    @Test
    @WithMockUser(username ="huw",  roles = "ADMIN")
    void deleteUserdWhileLoggedIn() throws Exception{

        LocalDateTime createdDate = LocalDateTime.now();

        Role role = new Role(1L, "ADMIN" );

        UserResponseDTO mockResponse = new UserResponseDTO(
                1, "huw", "hanni", "gmail", createdDate, role
        );

        //blev nødt til at bruge eq, da den ellers brokkede sig over, at der stod en "raw" string, er ikke helt sikker på hvorfor
        when(userService.getUserByUserId(eq(1), eq("huw"))).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    void deleteUserNotLoggedIn() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }









}
