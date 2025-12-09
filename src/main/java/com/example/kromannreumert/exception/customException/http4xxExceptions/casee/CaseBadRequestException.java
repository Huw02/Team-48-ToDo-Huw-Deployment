package com.example.kromannreumert.exception.customException.http4xxExceptions.casee;

import com.example.kromannreumert.exception.customException.http4xxExceptions.BadRequestException;
import com.example.kromannreumert.logging.entity.LogAction;

public class CaseBadRequestException extends BadRequestException {

    public CaseBadRequestException(LogAction action, String actor, String detail) {
        super(action, actor, "Case bad request: " + detail);
    }
}
