package com.example.booking_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Getter
@Setter
public class OpenBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID ownerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String additionalNotes;
    private boolean isActive;
    private List<String> walkerIds; // Lista de IDs de paseadores
    private LocalDateTime expiresAt; // Fecha de expiración
}
