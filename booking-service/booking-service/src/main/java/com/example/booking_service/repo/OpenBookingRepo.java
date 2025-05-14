package com.example.booking_service.repo;

import com.example.booking_service.model.OpenBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OpenBookingRepo extends JpaRepository<OpenBooking, UUID> {
    // Additional query methods if needed
}
