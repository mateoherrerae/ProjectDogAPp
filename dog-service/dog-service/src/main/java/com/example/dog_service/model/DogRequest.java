package com.example.dog_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


@Data
public class DogRequest {
    @NotBlank
    private String name;

    @NotNull
    private DogBreed breed;

    @Min(0)
    private int age;

    @Positive
    private Double weight;

    private String medicalHistory;

    @JsonProperty("specialCareInstructions") // 🔴 Si el JSON usa camelCase
    private String specialCareInstructions;

    @JsonProperty("isPublicProfile") // 🔴 Asegurar mapeo correcto
    private boolean isPublicProfile;
}
