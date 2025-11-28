package com.example.kromannreumert.unitTest.logging;

import com.example.kromannreumert.security.config.SecurityConfig;
import com.example.kromannreumert.logging.controller.LogController;
import com.example.kromannreumert.logging.service.LoggingService;
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


@WebMvcTest(controllers = LogController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class LoggingUnitTestController {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    LoggingService loggingService;


    @Test
    @WithMockUser(roles = "ADMIN") // <-- Mocks a user that has access
    void accessGetAllLogsWhileLoggedIn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/getalllogs"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test // No user credentials - so user cannot login.
    void accessDeniedToGetAllLogs() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/getalllogs"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "SAGSBEHANDLER")  // <--- Mocks a user that does not have access
    void accessDeniedToGetAllLogsWhileLoggedIn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/getalllogs"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

}
