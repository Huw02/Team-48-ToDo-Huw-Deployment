package com.example.kromannreumert.todo.service;

import com.example.kromannreumert.casee.entity.Casee;
import com.example.kromannreumert.casee.repository.CaseRepository;
import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.todo.dto.ToDoAssigneeUpdateRequest;
import com.example.kromannreumert.todo.dto.ToDoRequestDto;
import com.example.kromannreumert.todo.dto.ToDoRequestNewToDoDto;
import com.example.kromannreumert.todo.dto.ToDoResponseDto;
import com.example.kromannreumert.todo.entity.ToDo;
import com.example.kromannreumert.todo.mapper.ToDoMapper;
import com.example.kromannreumert.todo.repository.ToDoRepository;
import com.example.kromannreumert.user.entity.Role;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ToDoService {

    private final ToDoRepository toDoRepository;
    private final ToDoMapper toDoMapper;
    private final LoggingService loggingService;
    private final UserRepository userRepository;
    private final CaseRepository caseRepository;

    public ToDoService(ToDoRepository toDoRepository, ToDoMapper toDoMapper, LoggingService loggingService, UserRepository userRepository, CaseRepository caseRepository) {
        this.toDoRepository = toDoRepository;
        this.toDoMapper = toDoMapper;
        this.loggingService = loggingService;
        this.userRepository = userRepository;
        this.caseRepository = caseRepository;
    }

    public int getToDoSize() {
        List<ToDo> getAll = toDoRepository.findAll();
        return getAll.size();
    }

    public List<ToDoResponseDto> findAll(String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            Set<Role> roles = user.getRoles();

            boolean isJurist = hasRole(roles, "JURIST");
            boolean isSagsbehandler = hasRole(roles, "SAGSBEHANDLER");
            boolean isPartner = hasRole(roles, "PARTNER");
            boolean isAdmin = hasRole(roles, "ADMIN");

            List<ToDo> toDos;

            if (isJurist && !isSagsbehandler && !isPartner && !isAdmin) {
                toDos = toDoRepository.findDistinctByCaseId_Users_UsernameAndArchivedFalse(username);
            } else {
                toDos = toDoRepository.findAllByArchivedFalse();
            }

            List<ToDoResponseDto> responseDtos = toDos.stream()
                    .map(toDoMapper::toToDoResponseDto)
                    .toList();

            loggingService.log(LogAction.VIEW_ALL_TODOS, username, "Viewed todos");

            return responseDtos;
        } catch (Exception e) {
            loggingService.log(LogAction.VIEW_ALL_TODOS_FAILED, username, "Failed to view todos");
            throw new RuntimeException("Failed fetching todos", e);
        }
    }

    private boolean hasRole(Set<Role> roles, String roleName) {
        return roles.stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase(roleName));
    }

    public ToDoResponseDto findToDoById(String name, Long id) {
        try {
            ToDo toDo = toDoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

            ToDoResponseDto responseDto = toDoMapper.toToDoResponseDto(toDo);

            loggingService.log(LogAction.VIEW_ONE_TODO, name, "Viewed todo: " + toDo.getName());

            return responseDto;
        } catch (Exception e) {
            loggingService.log(LogAction.VIEW_ONE_TODO_FAILED, name, "Failed to view todo with id: " + id);

            throw new RuntimeException("Todo not found with id: " + id, e);
        }
    }

    public Set<User> getCaseAssigneesForTodo(Long todoId) {
        ToDo todo = toDoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found"));

        Casee casee = todo.getCaseId();
        return casee.getUsers();
    }

    public ToDoResponseDto updateAssignees(Long todoId, ToDoAssigneeUpdateRequest request, String name) {
        ToDo todo = toDoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found"));

        Set<User> newAssignees = request.userIds().stream()
                .map(id -> userRepository.findById(id.intValue())
                        .orElseThrow(() -> new EntityNotFoundException("User not found: " + id)))
                .collect(Collectors.toSet());

        todo.setUsers(newAssignees);

        if (request.userIds().size() > todo.getUsers().size()) {
            loggingService.log(LogAction.ADDED_USERS_TO_TODO, name, "Assigned users to: " + todo.getName());
        }

        if (request.userIds().size() > todo.getUsers().size()) {
            loggingService.log(LogAction.REMOVED_USERS_TO_TODO, name, "Removed users from: " + todo.getName());
        }

        ToDo saved = toDoRepository.save(todo);
        return toDoMapper.toToDoResponseDto(saved);
    }

    public ToDoResponseDto createToDo(String name, ToDoRequestNewToDoDto todoRequestDto) {
        try {
            Casee casee = caseRepository.findById(todoRequestDto.caseId())
                    .orElseThrow(() -> new EntityNotFoundException("Case not found"));
            ToDo toDo = toDoMapper.toToDo(todoRequestDto);
            toDo.setCaseId(casee);
            toDo = toDoRepository.save(toDo);

            loggingService.log(LogAction.CREATE_TODO, name, "Created a todo: " + toDo.getName());

            return toDoMapper.toToDoResponseDto(toDo);
        } catch (RuntimeException e) {
            loggingService.log(LogAction.CREATE_TODO_FAILED, name, "Failed to create todo: " + todoRequestDto.name());
            throw new RuntimeException("Could not create todo", e);
        }
    }

    public void deleteTodo(String name, Long id) {
        try {
            ToDo toDo = toDoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Could not find todo with id: " + id));

            toDoRepository.delete(toDo);

            loggingService.log(LogAction.DELETE_TODO, name, "Deleted todo: " + toDo.getName() + ", id: " + id);
        } catch (Exception e) {
            loggingService.log(LogAction.DELETE_TODO_FAILED, name, "Failed to delete todo with id: " + id + " " + e.getMessage());
            throw new RuntimeException("Could not delete todo with id: " + id, e);
        }
    }

    public ToDoResponseDto updateTodo(Long id, String name, ToDoRequestDto todoRequestDto) {
        try {
            ToDo todo = toDoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Could not find todo with id: " + id));

            todo.setName(todoRequestDto.name());
            todo.setDescription(todoRequestDto.description());
            todo.setStartDate(todoRequestDto.startDate());
            todo.setEndDate(todoRequestDto.endDate());
            todo.setUsers(todoRequestDto.toDoAssignees());
            todo.setPriority(todoRequestDto.priority());
            todo.setStatus(todoRequestDto.status());
            todo.setArchived(todoRequestDto.archived());

            toDoRepository.save(todo);
            loggingService.log(LogAction.UPDATE_TODO, name, "Updated todo: " + todoRequestDto.name());

            return toDoMapper.toToDoResponseDto(todo);

        } catch (Exception e) {
            loggingService.log(LogAction.UPDATE_TODO_FAILED, name, "Failed to update todo: " + todoRequestDto.name() + " " + e.getMessage());
            throw new RuntimeException("Could not update todo: " + todoRequestDto.name(), e);
        }
    }

    public List<ToDoResponseDto> findAssignedToUser(String username) {
        try {
            List<ToDo> toDos = toDoRepository.findDistinctByUsers_UsernameAndArchivedFalse(username);

            List<ToDoResponseDto> responseDtos = toDos.stream()
                    .map(toDoMapper::toToDoResponseDto)
                    .toList();

            loggingService.log(LogAction.VIEW_ALL_TODOS, username, "Viewed todos assigned to user");

            return responseDtos;
        } catch (Exception e) {
            loggingService.log(LogAction.VIEW_ALL_TODOS_FAILED, username, "Failed to view todos assigned to user");
            throw new RuntimeException("Failed fetching assigned todos", e);
        }
    }
}
