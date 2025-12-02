package com.example.kromannreumert.exception.customException;

public class UserUnauthorized extends ForbiddenException {
    public UserUnauthorized() {
        super("Invalid credentials");
    }
}
