package com.example.dog_service.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Dog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false) // 🔴 Asegúrate de que los nombres coincidan con la tabla
    private String name;

    @Enumerated(EnumType.STRING)
    private DogBreed breed;

    private int age;
    private Double weight; // ✅ Usar Double (no double primitivo) para permitir null

    @Column(name = "medical_history") // 🔴 Nombre de columna en snake_case
    private String medicalHistory;

    @Column(name = "special_care_instructions")
    private String specialCareInstructions;

    @Column(nullable = false)
    private UUID ownerId;

    @Column(name = "is_public_profile")
    private boolean isPublicProfile;
}