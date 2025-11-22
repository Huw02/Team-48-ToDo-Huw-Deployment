package com.example.kromannreumert.securityFeature.dto;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record JwtResponseDTO(String username, String token, Collection<? extends GrantedAuthority> role) {
}
