package com.example.booking_service.model;



import java.util.UUID;

public class Walker {
    private UUID id; // Identificador único del paseador
    private String userId; // ID del usuario en el User Service
    private double[] location; // Coordenadas [lat, lng]

    // Getters y setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }
}
