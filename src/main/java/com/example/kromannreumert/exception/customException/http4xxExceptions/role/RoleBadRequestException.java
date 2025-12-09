package com.example.kromannreumert.exception.customException.http4xxExceptions.role;

import com.example.kromannreumert.exception.customException.http4xxExceptions.BadRequestException;
import com.example.kromannreumert.logging.entity.LogAction;

public class RoleBadRequestException extends BadRequestException {

    public RoleBadRequestException(LogAction action, String actor, String detail) {
        super(action, actor, "Role bad request: " + detail);
    }
}
