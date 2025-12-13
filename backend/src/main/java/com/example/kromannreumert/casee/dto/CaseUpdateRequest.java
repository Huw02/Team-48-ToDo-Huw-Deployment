package com.example.kromannreumert.casee.dto;

import java.util.Set;

public record CaseUpdateRequest(
        Long id,
        String name,
        Long idPrefix,
        String responsibleUsername,
        Set<Integer> assigneeIds
) {

}

