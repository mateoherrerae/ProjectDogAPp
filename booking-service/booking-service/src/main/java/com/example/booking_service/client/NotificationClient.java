package com.example.booking_service.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.booking_service.model.NotificationRequest;

@FeignClient(
        name = "notification-service",
        url = "${notification-service.url}",
        configuration = FeignConfig.class
)
public interface NotificationClient {
    @PostMapping("/api/notifications/notify-walkers")
    ResponseEntity<Void> notifyWalkers(@RequestBody NotificationRequest request);
}
