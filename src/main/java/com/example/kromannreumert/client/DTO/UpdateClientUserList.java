package com.example.kromannreumert.client.DTO;

import java.util.Set;

public record UpdateClientUserList(Long clientIdPrefix, Set<String> user) {
}
