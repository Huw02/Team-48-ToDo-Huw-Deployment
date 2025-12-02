package com.example.kromannreumert.exception.controller;


import com.example.kromannreumert.exception.customException.ClientNotFoundException;
import com.example.kromannreumert.exception.customException.ForbiddenException;
import com.example.kromannreumert.exception.customException.NotFoundException;
import com.example.kromannreumert.exception.entity.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@Slf4j
@ControllerAdvice
public class ControllerExecptionHandler {


    /*

    Creating custom Exception handlers to catch errors and customize the content.
    Right now all the basic exceptions has been created.

    An example for a full exception created is "ClientNotFoundException"

    TODO create all the exception handlers

     */

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFound(NotFoundException ex, WebRequest req) {
        return buildResponse(404, ex, req);
    }

    // Change forbidden exception to Unauthorized
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorMessage> forbiddenAccess(ForbiddenException ex, WebRequest req) {
        return buildResponse(403, ex, req);
    }

    private ResponseEntity<ErrorMessage> buildResponse(int status, Exception ex, WebRequest req) {
        ErrorMessage msg = new ErrorMessage(
                status,
                new Date(),
                ex.getMessage(),
                req.getDescription(false)
        );
        return ResponseEntity.status(status).body(msg);
    }
}
