package com.example.booking_service.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DogResponse {
    private UUID id;
    private String name;
    private DogBreed breed;
    private int age;
    private boolean isPublicProfile; // Ensure this field exists
    private Double weight;
    private String medicalHistory;
    private String specialCareInstructions;
}
