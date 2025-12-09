package com.example.kromannreumert.exception.customException.http4xxExceptions.toDo;

import com.example.kromannreumert.exception.customException.http4xxExceptions.ConflictException;
import com.example.kromannreumert.logging.entity.LogAction;

public class ToDoConflictException extends ConflictException {

    public ToDoConflictException(LogAction action, String actor, String detail) {
        super(action, actor, "ToDo conflict: " + detail);
    }
}