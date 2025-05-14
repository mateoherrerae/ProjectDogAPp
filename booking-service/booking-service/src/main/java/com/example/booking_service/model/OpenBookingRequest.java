package com.example.booking_service.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OpenBookingRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String additionalNotes;
}
