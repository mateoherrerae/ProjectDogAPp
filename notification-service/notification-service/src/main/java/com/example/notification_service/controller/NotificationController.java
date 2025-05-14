package com.example.notification_service.controller;

import com.example.notification_service.model.NotificationRequest;
import com.example.notification_service.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/notify-walkers")
    public ResponseEntity<Void> notifyWalkers(@RequestBody NotificationRequest request) {
        // Lógica para enviar notificaciones a los paseadores
        notificationService.sendToWalkers(request);
        return ResponseEntity.ok().build();
    }
}
