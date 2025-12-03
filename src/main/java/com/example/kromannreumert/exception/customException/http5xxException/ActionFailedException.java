package com.example.kromannreumert.exception.customException.http5xxException;

import com.example.kromannreumert.logging.entity.LogAction;
import lombok.Getter;

@Getter
public class ActionFailedException extends RuntimeException {
    private final LogAction logAction;
    private final String actor;

    public ActionFailedException(LogAction logAction, String actor, Throwable message) {
        super(message);
        this.actor = actor;
        this.logAction = logAction;
    }

}
