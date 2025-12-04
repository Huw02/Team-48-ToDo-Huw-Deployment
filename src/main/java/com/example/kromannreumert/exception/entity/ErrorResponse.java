package com.example.kromannreumert.exception.entity;


public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        String action
) {}
