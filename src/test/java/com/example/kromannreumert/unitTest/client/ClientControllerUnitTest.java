package com.example.kromannreumert.unitTest.client;

import com.example.kromannreumert.client.controller.ClientController;
import com.example.kromannreumert.client.service.ClientService;
import com.example.kromannreumert.logging.controller.LogController;
import com.example.kromannreumert.security.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = ClientController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")

public class ClientControllerUnitTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ClientService clientService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void should_return_isOK_for_Admin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/client/"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @WithMockUser(roles = "SAGSBEHANDLER")
    void should_return_isOK_for_sagsbehandler() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/client/"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
