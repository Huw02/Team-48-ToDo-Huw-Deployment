package com.example.kromannreumert.client.DTO;

import java.util.Set;

public record ClientRequestDTO(String clientName, Set<String> users, Long idPrefix) {
}
