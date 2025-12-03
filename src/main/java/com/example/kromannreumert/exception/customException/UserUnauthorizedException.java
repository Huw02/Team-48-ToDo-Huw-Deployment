package com.example.kromannreumert.exception.customException;

public class UserUnauthorizedException extends UnauthorizedException {
    public UserUnauthorizedException() {
        super("Invalid credentials, try again");
    }
}
