package com.example.notification_service.repo;

import com.example.notification_service.model.NotificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepo extends JpaRepository<NotificationRequest, UUID> {

    // List<NotificationRequest> findByWalkerId(String walkerId);

}
