package com.example.kromannreumert.todo.dto;

import java.util.List;

public record ToDoAssigneeUpdateRequest(
        List<Long> userIds
) {
}
