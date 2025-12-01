package com.example.kromannreumert.client.DTO;

import com.example.kromannreumert.user.entity.User;

import java.util.List;

public record ClientResponeDTO(Long id, String name, List<String> users, Long idPrefix) {
}
