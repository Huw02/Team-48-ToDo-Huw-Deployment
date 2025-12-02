package com.example.kromannreumert.integrationTest.todo;

import com.example.kromannreumert.todo.dto.ToDoRequestDto;
import com.example.kromannreumert.todo.dto.ToDoRequestNewToDoDto;
import com.example.kromannreumert.todo.entity.Priority;
import com.example.kromannreumert.todo.entity.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ToDoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "jurist", roles = "JURIST")
    void findAllNotArchivedToDos() throws Exception {
        mockMvc.perform(get("/api/v1/todos")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].archived").value(false))
                .andExpect(jsonPath("$[1].archived").value(false));
    }

    @Test
    @WithMockUser(username = "jurist", roles = "JURIST")
    void findToDoById() throws Exception {
        mockMvc.perform(get("/api/v1/todos/{id}",1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("NDA"))
                .andExpect(jsonPath("$.description").value("Draft NDA"))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.startDate").value("2024-02-01"))
                .andExpect(jsonPath("$.endDate").value("2024-02-05"))
                .andExpect(jsonPath("$.archived").value(false))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("NOT_STARTED"));
    }

    @Test
    @WithMockUser(username = "jurist", roles = "JURIST")
    void createToDo() throws Exception {
        ToDoRequestNewToDoDto requestDto = new ToDoRequestNewToDoDto(
                "test",
                "dette er en test",
                LocalDate.of(2025,12,1),
                LocalDate.of(2025,12,2),
                Priority.MEDIUM
        );

        String requestBody = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.description").value("dette er en test"))
                .andExpect(jsonPath("$.startDate").value("2025-12-01"))
                .andExpect(jsonPath("$.endDate").value("2025-12-02"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.archived").value(false))
                .andExpect(jsonPath("$.status").value("NOT_STARTED"))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        Long id = objectMapper.readTree(responseJson).get("id").asLong();

        mockMvc.perform(get("/api/v1/todos/{id}",id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.status").value("NOT_STARTED"));
    }

    @Test
    @WithMockUser(username = "jurist", roles = "JURIST")
    void getTodo_returnsNotFoundForUnknownId() throws Exception {
        mockMvc.perform(get("/api/v1/todos/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "jurist", roles = "JURIST")
    void deleteToDo() throws Exception {
        mockMvc.perform(delete("/api/v1/todos/{id}", 1))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/todos/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "jurist", roles = "JURIST")
    void deleteToDo_nonExistingId_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/todos/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "jurist", roles = "JURIST")
    void updateToDo_existingId() throws Exception {
        ToDoRequestDto updateDto = new ToDoRequestDto(
                "Opdateret titel",
                "Opdateret beskrivelse",
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 15),
                Set.of(),
                Priority.MEDIUM,
                Status.IN_PROGRESS,
                false
        );

        String requestBody = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/api/v1/todos/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Opdateret titel"))
                .andExpect(jsonPath("$.description").value("Opdateret beskrivelse"))
                .andExpect(jsonPath("$.startDate").value("2025-01-10"))
                .andExpect(jsonPath("$.endDate").value("2025-01-15"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.archived").value(false));
    }

    @Test
    @WithMockUser(username = "jurist", roles = "JURIST")
    void updateToDo_nonExistingId() throws Exception {
        ToDoRequestDto updateDto = new ToDoRequestDto(
                "Ingen betydning",
                "Denne burde fejle",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 2),
                Set.of(),
                Priority.LOW,
                Status.NOT_STARTED,
                false
        );

        String requestBody = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/api/v1/todos/{id}", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "jurist", roles = "JURIST")
    void getToDoSize_returnsNumberOfTodosFromDatabase() throws Exception {
        mockMvc.perform(get("/api/v1/todos/size"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }
}
