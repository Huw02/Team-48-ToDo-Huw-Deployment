package com.example.kromannreumert.exception.customException.http4xxExceptions.client;

import com.example.kromannreumert.exception.customException.http4xxExceptions.ConflictException;
import com.example.kromannreumert.logging.entity.LogAction;

public class ClientConflictException extends ConflictException {

    public ClientConflictException(LogAction action, String actor, String detail) {
        super(action, actor, "Client conflict: " + detail);
    }
}