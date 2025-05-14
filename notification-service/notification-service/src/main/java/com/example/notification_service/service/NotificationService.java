package com.example.notification_service.service;

import com.example.notification_service.repo.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.notification_service.model.NotificationRequest;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private final NotificationRepo notificationRepo;

    @Autowired
    public NotificationService(NotificationRepo notificationRepo) {
        this.notificationRepo = notificationRepo;
    }
    public void sendToWalkers(NotificationRequest request) {
        // establish the time of expiration of the notification
        request.setExpiresAt(LocalDateTime.now().plusHours(24));

        // save the notification in the database
        notificationRepo.save(request);

        // Simulate sending notifications to walkers
        for (String walkerId : request.getWalkerIds()) {
            System.out.println("Sending notification to walker ID: " + walkerId);
        }
    }

}
