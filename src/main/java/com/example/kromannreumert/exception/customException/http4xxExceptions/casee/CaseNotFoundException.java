package com.example.kromannreumert.exception.customException.http4xxExceptions.casee;

import com.example.kromannreumert.exception.customException.http4xxExceptions.NotFoundException;
import com.example.kromannreumert.logging.entity.LogAction;

public class CaseNotFoundException extends NotFoundException {

    public CaseNotFoundException(LogAction action, String actor, String detail) {
        super(action, actor, "Case not found: " + detail);
    }
}
