package com.example.kromannreumert.security.dto;

import java.util.List;

public record JwtResponseDTO(String username, String token, List<String> role) {
}
