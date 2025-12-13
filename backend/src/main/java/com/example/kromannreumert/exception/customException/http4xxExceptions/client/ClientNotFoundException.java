package com.example.kromannreumert.exception.customException.http4xxExceptions.client;

import com.example.kromannreumert.exception.customException.http4xxExceptions.NotFoundException;
import com.example.kromannreumert.logging.entity.LogAction;

public class ClientNotFoundException extends NotFoundException {

    public ClientNotFoundException(LogAction action, String actor, String detail) {
        super(action, actor, "Client not found: " + detail);
    }

}
