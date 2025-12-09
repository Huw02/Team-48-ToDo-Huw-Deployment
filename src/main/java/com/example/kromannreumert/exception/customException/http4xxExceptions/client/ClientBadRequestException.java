package com.example.kromannreumert.exception.customException.http4xxExceptions.client;

import com.example.kromannreumert.exception.customException.http4xxExceptions.BadRequestException;
import com.example.kromannreumert.logging.entity.LogAction;

public class ClientBadRequestException extends BadRequestException {

    public ClientBadRequestException(LogAction action, String actor, String detail) {
        super(action, actor, "Invalid Client Request: " + detail);
    }
}
