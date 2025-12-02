package com.example.kromannreumert.exception.customException;

public class UserUnauthorized extends UnauthorizedException {
    public UserUnauthorized() {
        super("Invalid credentials");
    }
}
