package com.example.kromannreumert.exception.controller;


import com.example.kromannreumert.exception.customException.http4xxExceptions.BadRequestException;
import com.example.kromannreumert.exception.customException.http4xxExceptions.ConflictException;
import com.example.kromannreumert.exception.customException.http4xxExceptions.NotFoundException;
import com.example.kromannreumert.exception.customException.http5xxException.ActionFailedException;
import com.example.kromannreumert.exception.entity.ErrorResponse;
import com.example.kromannreumert.logging.service.LoggingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private final LoggingService loggingService;

    public GlobalExceptionHandler(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    /**
     * Method used to handle HTTP code 404 not found elements from the database for all entities "Client/User/Case/To-Do"
     * @param ex is the customized parameter in the exception handler that returns "Client/User/Case/To-do" not found and a message
     * @param request is the endpoint url that the user has been trying to access
     * @return customized exception object that returns HTTP status code from record: ErrorResponse, customized exception text and the endpoint visited
     */

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound404(NotFoundException ex, WebRequest request) {


        log.error("Exception not found occurred:  {}", ex.getMessage());
        loggingService.log(
                ex.getAction(),
                ex.getActor(),
                ex.getMessage()
        );

        return ResponseEntity.status(404).body(
                new ErrorResponse(404,
                        "Not Found",
                        ex.getMessage(),
                        request.getDescription(true),
                        ex.getAction().name())
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest400(BadRequestException ex, WebRequest request) {


        log.error("Bad request occurred: {}", ex.getMessage());
        loggingService.log(
                ex.getAction(),
                ex.getActor(),
                ex.getMessage()
        );

        return ResponseEntity.status(400).body(
                new ErrorResponse(
                        400,
                        "Bad Request",
                        ex.getMessage(),
                        request.getDescription(true),
                        ex.getAction().name())
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict409(ConflictException ex, WebRequest request) {

        log.error("Conflict occurred: {}", ex.getMessage());
        loggingService.log(
                ex.getAction(),
                ex.getActor(),
                ex.getMessage()
        );

        return ResponseEntity.status(409).body(
                new ErrorResponse(
                        409,
                        "Conflict",
                        ex.getMessage(),
                        request.getDescription(true),
                        ex.getAction().name())
        );
    }


    /**
     * Method used to handle HTTP code 5xx internal server error for all methods in the system. It displays the log action for the user
     * but in the terminal it displays the Exception that has been caught
     * @param ex is the error caught and converted to our customized Audit trail to display for the user such as "View_all_failed"
     * @return the customized global exception to the requester with the customized audit log
     */

    @ExceptionHandler(ActionFailedException.class)
    public ResponseEntity<ErrorResponse> handle500(ActionFailedException ex, WebRequest request) {

        log.error(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage(), ex);

            loggingService.log(
                ex.getLogAction(),
                ex.getActor(),
                ex.getCause().getMessage()
        );

        return ResponseEntity.status(500).body(new ErrorResponse(
                500,
                "Internal Server Error",
                ex.getCause() != null ? ex.getCause().getMessage() : "Unknown error",
                request.getDescription(true),
                ex.getLogAction().name()
                )
        );
    }

    /*
    /**
     * Method used to handle user not authorized when trying to log in
     * @param ex is the customized parameter in the exception handler (Invalid user credentials)
     * @param req is the endpoint that has been accessed
     * @return customized exception object that returns HTTP status code, customized exception text and the endpoint visited
     */

//    // Change forbidden exception to Unauthorized
//    @ExceptionHandler(UnauthorizedException.class)
//    public ResponseEntity<ErrorMessage> unAuthorized(UnauthorizedException ex, WebRequest req) {
//        return buildResponse(401, ex, req);
//    }


}
