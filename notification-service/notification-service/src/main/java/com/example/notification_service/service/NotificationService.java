package com.example.notification_service.service;

import com.example.notification_service.model.Notification;
import com.example.notification_service.model.NotificationRequest;
import com.example.notification_service.model.NotificationStatus;
import com.example.notification_service.repo.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepo notificationRepo;


    public void sendToWalkers(NotificationRequest request) {
        // Establish the time of expiration of the notification
        request.setExpiresAt(LocalDateTime.now().plusHours(24));

        // Convert NotificationRequest to Notification
        Notification notification = Notification.builder()
                .status(NotificationStatus.PENDING)
                .recipientId(null) // Set recipientId if applicable
                .message(request.getMessage())
                .expiresAt(request.getExpiresAt())
                .metadata(new HashMap<>()) // Initialize metadata
                .build();

        // Save the notification in the database
        notificationRepo.save(notification);

        // Simulate sending notifications to walkers
        for (String walkerId : request.getWalkerIds()) {
            System.out.println("Sending notification to walker ID: " + walkerId);
        }
    }

    @Async
    public void sendToWalkersAsync(NotificationRequest request) {
        try {
            Notification notification = Notification.builder()
                    .status(NotificationStatus.PENDING)
                    .recipientId(null)
                    .message(request.getMessage())
                    .expiresAt(request.getExpiresAt())
                    .metadata(new HashMap<>()) // Initialize metadata
                    .build();

            notificationRepo.save(notification);

            for (String walkerId : request.getWalkerIds()) {
                log.info("Sending notification asynchronously to walker ID: {}", walkerId);
            }
        } catch (Exception e) {
            log.error("Error while sending notifications asynchronously: {}", e.getMessage(), e);
        }
    }
}