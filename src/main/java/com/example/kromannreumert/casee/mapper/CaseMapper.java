package com.example.kromannreumert.casee.mapper;

import com.example.kromannreumert.casee.dto.CaseResponseDTO;
import com.example.kromannreumert.casee.entity.Casee;
import org.springframework.stereotype.Component;

@Component
public class CaseMapper {


    public CaseResponseDTO caseToResponse(Casee casee) {
        return new CaseResponseDTO(casee.getName(), casee.getClient(), casee.getUsers(), casee.getIdPrefix());
    }

}
