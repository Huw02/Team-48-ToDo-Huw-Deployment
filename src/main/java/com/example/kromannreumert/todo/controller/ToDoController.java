package com.example.kromannreumert.todo.controller;

import com.example.kromannreumert.todo.dto.*;
import com.example.kromannreumert.todo.service.ToDoService;
import com.example.kromannreumert.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/v1")
@CrossOrigin("*")
public class ToDoController {

    private final ToDoService toDoService;

    public ToDoController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @GetMapping("/todos")
    public ResponseEntity<List<ToDoResponseDto>> findAll(Principal principal) {
            List<ToDoResponseDto> responseDtos = toDoService.findAll(principal.getName());
            return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/todos/assigned")
    public ResponseEntity<List<ToDoResponseDto>> findAssigned(Principal principal) {
        List<ToDoResponseDto> responseDtos = toDoService.findAssignedToUser(principal.getName());
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/todos/{id}/case-assignees")
    public ResponseEntity<Set<User>> getCaseAssigneesForTodo(@PathVariable Long id) {
        Set<User> users = toDoService.getCaseAssigneesForTodo(id);
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/todos/{id}/assignees")
    public ResponseEntity<ToDoResponseDto> updateAssignees(
            @PathVariable Long id,
            @RequestBody ToDoAssigneeUpdateRequest request,
            Principal principal
    ) {
        ToDoResponseDto updated = toDoService.updateAssignees(id, request, principal.getName());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<ToDoResponseDto> findById(@PathVariable Long id, Principal principal) {
        ToDoResponseDto responseDto = toDoService.findToDoById(principal.getName(),id);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/todos")
    public ResponseEntity<ToDoResponseDto> createToDo(@RequestBody ToDoRequestNewToDoDto todoRequestDto, Principal principal) {
        ToDoResponseDto responseDto = toDoService.createToDo(principal.getName(), todoRequestDto);
        URI location = URI.create("/todos" + responseDto.id());
        return ResponseEntity.created(location).body(responseDto);
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Void> deleteToDo(@PathVariable Long id, Principal principal) {
        toDoService.deleteTodo(principal.getName(),id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/todos/{id}")
    public ResponseEntity<ToDoResponseDto> updateTodo(@PathVariable Long id, @RequestBody ToDoRequestDto todoRequestDto, Principal principal) {
        ToDoResponseDto toDoResponseDto = toDoService.updateTodo(id, principal.getName(), todoRequestDto);
        return ResponseEntity.ok(toDoResponseDto);
    }

    @GetMapping("/todos/size")
    public ResponseEntity<Integer> getToDoSize() {
        Integer toDoSize = toDoService.getToDoSize();
        return ResponseEntity.ok(toDoSize);
    }
}
