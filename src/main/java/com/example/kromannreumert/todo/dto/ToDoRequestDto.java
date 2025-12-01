package com.example.kromannreumert.todo.dto;

import com.example.kromannreumert.todo.entity.Priority;
import com.example.kromannreumert.todo.entity.Status;
import com.example.kromannreumert.user.entity.User;

import java.time.LocalDate;
import java.util.Set;

public record ToDoRequestDto(
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        Set<User> toDoAssignees,
        Priority priority,
        Status status,
        Boolean archived
) {}
