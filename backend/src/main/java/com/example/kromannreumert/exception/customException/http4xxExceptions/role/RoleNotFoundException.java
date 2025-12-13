package com.example.kromannreumert.exception.customException.http4xxExceptions.role;

import com.example.kromannreumert.exception.customException.http4xxExceptions.NotFoundException;
import com.example.kromannreumert.logging.entity.LogAction;

public class RoleNotFoundException extends NotFoundException {

    public RoleNotFoundException(LogAction action, String actor, String detail) {
        super(action, actor, "Role not found: " + detail);
    }
}
