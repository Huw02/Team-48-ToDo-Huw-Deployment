package com.example.kromannreumert.exception.customException.http4xxExceptions.toDo;

import com.example.kromannreumert.exception.customException.http4xxExceptions.BadRequestException;
import com.example.kromannreumert.logging.entity.LogAction;

public class ToDoBadReqeustException extends BadRequestException {

    public ToDoBadReqeustException(LogAction action, String actor, String detail) {
        super(action, actor, "Invalid ToDo Request: " + detail);
    }
}
