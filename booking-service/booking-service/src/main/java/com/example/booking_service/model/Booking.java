package com.example.booking_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId; // From User Service

    @Column(name = "walker_user_id", nullable = false) // ← Agrega name="walker_id"
    private UUID walkerUserId; // From Walker Service

    @Column(name = "walker_id", nullable = false)
    private UUID walkerId;

    @ElementCollection
    @CollectionTable(name = "booking_dogs", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "dog_id", nullable = false)
    private List<UUID> dogIds; // From Dog Service

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "meeting_point", nullable = false)
    private String meetingPoint;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status; // PENDING, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED

    @Column(name = "price", nullable = false)
    private Double price;
    // initialize status to PENDING
    public Booking() {
        this.status = BookingStatus.PENDING; // Inicialización explícita
    }
}