package com.example.notification_service.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;


// the dog walker is the one that accepts the booking
// automatic booking is when the system automatically assigns a walker
@Data
public class OpenBooking {
    private UUID id;
    private UUID ownerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String additionalNotes;
    private boolean isActive;
}
