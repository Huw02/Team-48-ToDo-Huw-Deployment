package com.example.kromannreumert.user.dto;

import com.example.kromannreumert.user.entity.Role;

import java.time.LocalDateTime;
import java.util.Date;

public record UserResponseDTO(long userId, String username, String name, String email, LocalDateTime createdDate, Role role) {
}
