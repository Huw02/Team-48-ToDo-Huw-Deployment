package com.example.kromannreumert.user.dto;

import com.example.kromannreumert.user.entity.Role;

import java.util.Date;

public record UserResponseDTO(int userId, String username, String name, String email, Date createdDate, Role role) {
}
