package com.example.notification_service.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class NotificationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private List<String> walkerIds; // IDs of the walkers
    private String message;         // message of the notification
    private String bookingId;       // ID of the booking
    private LocalDateTime expiresAt;   // ttl
}
