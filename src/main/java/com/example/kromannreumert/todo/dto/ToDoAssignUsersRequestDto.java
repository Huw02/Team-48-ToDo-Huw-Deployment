package com.example.kromannreumert.todo.dto;

import java.util.Set;

public record ToDoAssignUsersRequestDto(
        Set<Long> userIds
) {
}
