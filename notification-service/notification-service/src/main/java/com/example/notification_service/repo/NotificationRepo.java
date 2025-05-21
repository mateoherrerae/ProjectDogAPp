package com.example.notification_service.repo;

import com.example.notification_service.model.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepo extends CrudRepository<Notification, UUID> {
}
