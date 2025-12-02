package com.example.kromannreumert.unitTest.todo;

import com.example.kromannreumert.security.config.SecurityConfig;
import com.example.kromannreumert.todo.controller.ToDoController;
import com.example.kromannreumert.todo.dto.ToDoRequestDto;
import com.example.kromannreumert.todo.dto.ToDoRequestNewToDoDto;
import com.example.kromannreumert.todo.dto.ToDoResponseDto;
import com.example.kromannreumert.todo.entity.Priority;
import com.example.kromannreumert.todo.entity.Status;
import com.example.kromannreumert.todo.service.ToDoService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ToDoController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class ToDoUnitTestController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ToDoService toDoService;

    @Test
    void getAllTodosNotLoggedIn() throws Exception{
        mockMvc.perform(get("/api/v1/todos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTodosWhileLoggedInAsAdmin() throws Exception {
        when(toDoService.findAll(anyString())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "partner", roles = "PARTNER")
    void getAllTodosWhileLoggedInAsPartner() throws Exception {
        when(toDoService.findAll(anyString())).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "sagsbehandler", roles = "SAGSBEHANDLER")
    void getAllTodosWhileLoggedInAsSagsbehandler() throws Exception {
        when(toDoService.findAll(anyString())).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "jurist", roles = "JURIST")
    void getAllTodosWhileLoggedInAsJurist() throws Exception {
        when(toDoService.findAll(anyString())).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "jurist", roles = "JURIST")
    void getToDoByIdWhileLoggedIn() throws Exception {
        ToDoResponseDto responseDto = new ToDoResponseDto(
                1L,
                "ToDoTest",
                "Dette er en test",
                LocalDateTime.now(),
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                Set.of(),
                Priority.MEDIUM,
                Status.NOT_STARTED,
                false
        );

        Long id = responseDto.id();

        when(toDoService.findToDoById("jurist", id))
                .thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/todos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(responseDto.name()))
                .andExpect(jsonPath("$.description").value(responseDto.description()))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.startDate").value(responseDto.startDate().toString()))
                .andExpect(jsonPath("$.endDate").value(responseDto.endDate().toString()))
                .andExpect(jsonPath("$.toDoAssignees").isArray())
                .andExpect(jsonPath("$.priority").value(responseDto.priority().name()))
                .andExpect(jsonPath("$.status").value(responseDto.status().name()))
                .andExpect(jsonPath("$.archived").value(responseDto.archived()));
    }

    @Test
    void getToDoByIdNotLoggedIn() throws Exception {
        mockMvc.perform( get("/api/v1/todos/{id}", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "jurist", roles = "JURIST")
    void postToDoWhileLoggedIn() throws Exception {
        ToDoRequestNewToDoDto requestDto = new ToDoRequestNewToDoDto(
                "test",
                "dette er en test",
                LocalDate.of(2025,12,1),
                LocalDate.of(2025,12,2),
                Priority.MEDIUM
        );

        ToDoResponseDto responseDto = new ToDoResponseDto(
                1L,
                requestDto.name(),
                requestDto.description(),
                LocalDateTime.now(),
                requestDto.startDate(),
                requestDto.endDate(),
                Set.of(),
                requestDto.priority(),
                Status.NOT_STARTED,
                false
        );

        when(toDoService.createToDo("jurist", requestDto)).thenReturn(responseDto);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.description").value("dette er en test"))
                .andExpect(jsonPath("$.startDate").value(requestDto.startDate().toString()))
                .andExpect(jsonPath("$.endDate").value(requestDto.endDate().toString()))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.status").value("NOT_STARTED"))
                .andExpect(jsonPath("$.archived").value(false));
    }

    @Test
    void postToDoNotLoggedIn() throws Exception {
        mockMvc.perform(post("/api/v1/todos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "jurist", roles = "JURIST")
    void deleteToDoWhileLoggedIn() throws Exception {
        Long id = 1L;

        doNothing().when(toDoService).deleteTodo("jurist", id);

        mockMvc.perform(delete("/api/v1/todos/{id}", id))
                .andExpect(status().isNoContent());

        verify(toDoService).deleteTodo("jurist",id);
    }

    @Test
    @WithMockUser(username = "jurist", roles = "JURIST")
    void deleteToDoNotFoundWhileLoggedIn() throws Exception {
        Long id = 999L;

        doThrow(new RuntimeException("Todo not found"))
                .when(toDoService).deleteTodo("jurist", id);

        mockMvc.perform(delete("/api/v1/todos/{id}", id))
                .andExpect(status().isNotFound());

        verify(toDoService).deleteTodo("jurist", id);
    }

    @Test
    void deleteToDoNotLoggedIn() throws Exception {
        mockMvc.perform(delete("/api/v1/todos/{id}",1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "jurist", roles = "JURIST")
    void updateToDoWhileLoggedIn() throws Exception {
        Long id = 1L;

        ToDoRequestDto requestDto = new ToDoRequestDto(
                "test",
                "dette er en update test",
                LocalDate.of(2025,12,1),
                LocalDate.of(2025,12,2),
                Set.of(),
                Priority.MEDIUM,
                Status.NOT_STARTED,
                false
        );

        ToDoResponseDto responseDto = new ToDoResponseDto(
                1L,
                requestDto.name(),
                requestDto.description(),
                LocalDateTime.now(),
                requestDto.startDate(),
                requestDto.endDate(),
                Set.of(),
                requestDto.priority(),
                Status.NOT_STARTED,
                false
        );

        when(toDoService.updateTodo(id, "jurist", requestDto)).thenReturn(responseDto);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/api/v1/todos/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(responseDto.name()))
                .andExpect(jsonPath("$.description").value(responseDto.description()))
                .andExpect(jsonPath("$.startDate").value(responseDto.startDate().toString()))
                .andExpect(jsonPath("$.endDate").value(responseDto.endDate().toString()))
                .andExpect(jsonPath("$.priority").value(responseDto.priority().toString()))
                .andExpect(jsonPath("$.status").value(responseDto.status().toString()))
                .andExpect(jsonPath("$.archived").value(responseDto.archived()));
    }

    @Test
    @WithMockUser(username = "jurist", roles = "JURIST")
    void updateToDoNotFound() throws Exception {
        Long id = 999L;

        ToDoRequestDto requestDto = new ToDoRequestDto(
                "test",
                "update",
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 2),
                Set.of(),
                Priority.MEDIUM,
                Status.NOT_STARTED,
                false
        );

        doThrow(new RuntimeException("Todo not found"))
                .when(toDoService).updateTodo(id, "jurist", requestDto);

        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/api/v1/todos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateToDoNotLoggedIn() throws Exception {
        mockMvc.perform(put("/api/v1/todos/{id}",1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "JURIST")
    void getToDosSizeWhileLoggedIn() throws Exception {
        when(toDoService.getToDoSize()).thenReturn(5);
        mockMvc.perform(get("/api/v1/todos/size"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }

    @Test
    void getToDosSizeNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/v1/todos/size"))
                .andExpect(status().isUnauthorized());
    }
}
