package com.example.kromannreumert.exception.customException.http4xxExceptions;

import com.example.kromannreumert.logging.entity.LogAction;

public abstract class BadRequestException extends ApiBusinessException {
    public BadRequestException(LogAction action, String actor, String message) {
        super(action, actor, message);
    }
}
