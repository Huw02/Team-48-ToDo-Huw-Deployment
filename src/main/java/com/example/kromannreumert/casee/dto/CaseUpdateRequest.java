package com.example.kromannreumert.casee.dto;

import com.example.kromannreumert.user.entity.User;

import java.util.Set;

public record CaseUpdateRequest(
        Long id,
        String name,
        Long idPrefix,
        Integer responsibleUserId,
        Set<Integer> assigneeIds
) {

}

