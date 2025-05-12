package com.example.booking_service.repo;


import com.example.booking_service.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookingRepo extends JpaRepository<Booking, UUID> {
    boolean existsByWalkerUserIdAndOwnerId(UUID walkerUserId, UUID ownerId);
}
