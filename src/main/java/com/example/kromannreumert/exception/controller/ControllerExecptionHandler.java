package com.example.kromannreumert.exception.controller;


import com.example.kromannreumert.exception.customException.ClientNotFoundException;
import com.example.kromannreumert.exception.customException.UnauthorizedException;
import com.example.kromannreumert.exception.customException.NotFoundException;
import com.example.kromannreumert.exception.customException.UnauthorizedException;
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

    /**
     * Method used to handle not found elements from the database
     * @param ex is the customized parameter in the exception handler that returns "Client/User/Case/To-do" not found and the id
     * @param req is the endpoint that has been accessed
     * @return customized exception object that returns HTTP status code, customized exception text and the endpoint visited
     */

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFound(NotFoundException ex, WebRequest req) {
        return buildResponse(404, ex, req);
    }

    /**
     * Method used to handle user not authorized when trying to log in
     * @param ex is the customized parameter in the exception handler (Invalid user credentials)
     * @param req is the endpoint that has been accessed
     * @return customized exception object that returns HTTP status code, customized exception text and the endpoint visited
     */
    
    // Change forbidden exception to Unauthorized
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorMessage> unAuthorized(UnauthorizedException ex, WebRequest req) {
        return buildResponse(401, ex, req);
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
