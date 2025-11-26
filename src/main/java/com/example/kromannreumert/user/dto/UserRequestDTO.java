package com.example.kromannreumert.user.dto;

import java.util.Date;

public record UserRequestDTO(String username, String name, String email, String password, Date createdDate, int roleId) {
}
