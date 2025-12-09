package com.example.kromannreumert.exception.customException.http4xxExceptions;

import com.example.kromannreumert.logging.entity.LogAction;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(LogAction action, String actor, String detail) {
        super(action, actor, "User not found: " + detail);
    }}
