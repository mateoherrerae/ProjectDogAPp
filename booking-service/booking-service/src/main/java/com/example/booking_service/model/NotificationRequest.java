package com.example.booking_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private List<String> walkerIds;
    private String message;
    private String bookingId;
    private LocalDateTime expiresAt;

    public NotificationRequest(List<Walker> walkers, String message, LocalDateTime expiresAt) {
        this.walkerIds = walkers.stream()
                .map(Walker::getId)
                .map(UUID::toString)
                .collect(Collectors.toList());
        this.message = message;
        this.expiresAt = expiresAt;
    }
}