package com.example.kromannreumert.exception.customException;

public class ClientNotFoundException extends NotFoundException{
    public ClientNotFoundException(Long id) {
        super("Client with ID " + id + " not found");
    }
}
