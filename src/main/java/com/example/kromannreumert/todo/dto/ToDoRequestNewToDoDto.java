package com.example.kromannreumert.todo.dto;

import com.example.kromannreumert.todo.entity.Priority;

import java.time.LocalDate;

public record ToDoRequestNewToDoDto(
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        Priority priority
) {}
