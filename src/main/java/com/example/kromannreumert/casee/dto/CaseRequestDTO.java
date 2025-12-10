package com.example.kromannreumert.casee.dto;

import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.user.entity.User;

import java.util.Set;

public record CaseRequestDTO(
        String name,
        Long clientId,
        Set<Integer> assigneeIds,
        Long idPrefix,
        String responsibleUsername
) {}
