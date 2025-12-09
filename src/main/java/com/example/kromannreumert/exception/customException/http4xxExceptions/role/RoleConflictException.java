package com.example.kromannreumert.exception.customException.http4xxExceptions.role;

import com.example.kromannreumert.exception.customException.http4xxExceptions.ConflictException;
import com.example.kromannreumert.logging.entity.LogAction;

public class RoleConflictException extends ConflictException {

    public RoleConflictException(LogAction action, String actor, String detail) {
        super(action, actor, "Role conflict: " + detail);
    }
}
