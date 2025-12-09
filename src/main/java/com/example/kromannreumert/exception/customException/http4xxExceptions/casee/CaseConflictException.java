package com.example.kromannreumert.exception.customException.http4xxExceptions.casee;

import com.example.kromannreumert.exception.customException.http4xxExceptions.ConflictException;
import com.example.kromannreumert.logging.entity.LogAction;

public class CaseConflictException extends ConflictException {

    public CaseConflictException(LogAction action, String actor, String detail) {
        super(action, actor, "Case conflict: " + detail);
    }
}
