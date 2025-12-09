package com.example.kromannreumert.exception.customException.http4xxExceptions;

import com.example.kromannreumert.logging.entity.LogAction;

public abstract class ConflictException extends ApiBusinessException{
    public ConflictException(LogAction action, String actor, String message) {
        super(action, actor, message);
    }
}
