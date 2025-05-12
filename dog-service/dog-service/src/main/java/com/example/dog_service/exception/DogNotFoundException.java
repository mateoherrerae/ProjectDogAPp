package com.example.dog_service.exception;


import java.util.UUID;

public class DogNotFoundException extends RuntimeException {
    public DogNotFoundException(UUID dogId) {
        super("Dog not found with ID: " + dogId);
    }
}