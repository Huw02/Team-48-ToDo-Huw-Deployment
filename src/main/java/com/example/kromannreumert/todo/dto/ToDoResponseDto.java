package com.example.kromannreumert.todo.dto;

import com.example.kromannreumert.todo.entity.Priority;
import com.example.kromannreumert.todo.entity.Status;
import com.example.kromannreumert.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record ToDoResponseDto(
        Long id,
        String name,
        String description,
        LocalDateTime created,
        LocalDate startDate,
        LocalDate endDate,
        Set<User> toDoAssignees,
        Priority priority,
        Status status,
        Boolean archived
) {}
