package com.example.kromannreumert.todo.controller;

import com.example.kromannreumert.todo.dto.ToDoRequestDto;
import com.example.kromannreumert.todo.dto.ToDoRequestNewToDoDto;
import com.example.kromannreumert.todo.dto.ToDoResponseDto;
import com.example.kromannreumert.todo.service.ToDoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

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
        try {
            List<ToDoResponseDto> responseDtos = toDoService.findAll(principal.getName());
            return ResponseEntity.ok(responseDtos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/todos/assigned")
    public ResponseEntity<List<ToDoResponseDto>> findAssigned(Principal principal) {
        try {
            List<ToDoResponseDto> responseDtos = toDoService.findAssignedToUser(principal.getName());
            return ResponseEntity.ok(responseDtos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<ToDoResponseDto> findById(@PathVariable Long id, Principal principal) {
        try {
            ToDoResponseDto responseDto = toDoService.findToDoById(principal.getName(),id);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/todos")
    public ResponseEntity<ToDoResponseDto> createToDo(@RequestBody ToDoRequestNewToDoDto todoRequestDto, Principal principal) {
        try {
            ToDoResponseDto responseDto = toDoService.createToDo(principal.getName(), todoRequestDto);
            URI location = URI.create("/todos" + responseDto.id());
            return ResponseEntity.created(location).body(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Void> deleteToDo(@PathVariable Long id, Principal principal) {
        try {
            toDoService.deleteTodo(principal.getName(),id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/todos/{id}")
    public ResponseEntity<ToDoResponseDto> updateTodo(@PathVariable Long id, @RequestBody ToDoRequestDto todoRequestDto, Principal principal) {
        try {
            ToDoResponseDto toDoResponseDto = toDoService.updateTodo(id, principal.getName(), todoRequestDto);
            return ResponseEntity.ok(toDoResponseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/todos/size")
    public ResponseEntity<Integer> getToDoSize() {
        try {
            Integer toDoSize = toDoService.getToDoSize();
            return ResponseEntity.ok(toDoSize);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        }
    }
}
