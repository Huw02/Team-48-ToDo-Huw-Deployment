package com.example.kromannreumert.exception.customException.http4xxExceptions.toDo;

import com.example.kromannreumert.exception.customException.http4xxExceptions.NotFoundException;
import com.example.kromannreumert.logging.entity.LogAction;

public class ToDoNotFoundException extends NotFoundException {

    public ToDoNotFoundException(LogAction action, String actor, String detail) {
        super(action, actor, "ToDo not found: " + detail);
    }

}
