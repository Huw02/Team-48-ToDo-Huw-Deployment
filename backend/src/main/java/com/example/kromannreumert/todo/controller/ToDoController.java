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
@RequestMapping("api/v1/todos")
@CrossOrigin("*")
public class ToDoController {

    private final ToDoService toDoService;

    public ToDoController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @GetMapping("")
    public ResponseEntity<List<ToDoResponseDto>> findAll(Principal principal) {
            List<ToDoResponseDto> responseDtos = toDoService.findAll(principal.getName());
            return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<ToDoResponseDto>> findAssigned(Principal principal) {
        List<ToDoResponseDto> responseDtos = toDoService.findAssignedToUser(principal.getName());
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/{id}/case-assignees")
    public ResponseEntity<Set<User>> getCaseAssigneesForTodo(@PathVariable Long id) {
        Set<User> users = toDoService.getCaseAssigneesForTodo(id);
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}/assignees")
    public ResponseEntity<ToDoResponseDto> updateAssignees(
            @PathVariable Long id,
            @RequestBody ToDoAssigneeUpdateRequest request,
            Principal principal
    ) {
        ToDoResponseDto updated = toDoService.updateAssignees(id, request, principal.getName());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToDoResponseDto> findById(@PathVariable Long id, Principal principal) {
        ToDoResponseDto responseDto = toDoService.findToDoById(principal.getName(),id);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("")
    public ResponseEntity<ToDoResponseDto> createToDo(@RequestBody ToDoRequestNewToDoDto todoRequestDto, Principal principal) {
        ToDoResponseDto responseDto = toDoService.createToDo(principal.getName(), todoRequestDto);
        URI location = URI.create("/todos" + responseDto.id());
        return ResponseEntity.created(location).body(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteToDo(@PathVariable Long id, Principal principal) {
        toDoService.deleteTodo(principal.getName(),id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ToDoResponseDto> updateTodo(@PathVariable Long id, @RequestBody ToDoRequestDto todoRequestDto, Principal principal) {
        ToDoResponseDto toDoResponseDto = toDoService.updateTodo(id, principal.getName(), todoRequestDto);
        return ResponseEntity.ok(toDoResponseDto);
    }

    @GetMapping("/size")
    public ResponseEntity<Integer> getToDoSize() {
        Integer toDoSize = toDoService.getToDoSize();
        return ResponseEntity.ok(toDoSize);
    }

    @GetMapping("/search-by-assignee")
    public List<ToDoResponseDto> searchByAssignee(@RequestParam String q) {
        return toDoService.findByAssigneeSearch(q);
    }
}
