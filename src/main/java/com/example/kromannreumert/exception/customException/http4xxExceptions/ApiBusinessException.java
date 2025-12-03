package com.example.kromannreumert.exception.customException.http4xxExceptions;


import com.example.kromannreumert.logging.entity.LogAction;
import lombok.Getter;

@Getter
public abstract class ApiBusinessException extends RuntimeException {
    private final LogAction action;
    private final String actor;

    public ApiBusinessException(LogAction action, String actor, String message) {
        super(message);
        this.action = action;
        this.actor = actor;
    }

}
