package com.example.booking_service.service;

import com.example.booking_service.client.UserServiceClient;
import com.example.booking_service.client.WalkerClient;
import com.example.booking_service.model.Booking;
import com.example.booking_service.repo.BookingRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepo bookingRepo;
    private final WalkerClient walkerClient;
    private final UserServiceClient userServiceClient;

    public BookingService(BookingRepo bookingRepo, WalkerClient walkerClient, UserServiceClient userServiceClient) {
        this.bookingRepo = bookingRepo;
        this.walkerClient = walkerClient;
        this.userServiceClient = userServiceClient;
    }

    public Booking createBooking(UUID walkerUserId, UUID clientId, Booking booking) {
        // Validar paseador
        ResponseEntity<Boolean> walkerValidationResponse = walkerClient.checkWalkerProfileExists(walkerUserId);
        if (walkerValidationResponse.getStatusCode() != HttpStatus.OK || !walkerValidationResponse.getBody()) {
            throw new IllegalArgumentException("Walker no válido o no verificado");
        }

        // Validar cliente
        ResponseEntity<String> clientValidationResponse = userServiceClient.getUsernameById(clientId);
        if (clientValidationResponse.getStatusCode() != HttpStatus.OK || clientValidationResponse.getBody() == null) {
            throw new IllegalArgumentException("Cliente no válido");
        }

        // Guardar la reserva
        booking.setWalkerUserId(walkerUserId);
        booking.setOwnerId(clientId);
        return bookingRepo.save(booking);
    }

    public boolean bookingExists(String walkerUserId, String clientId) {
        return bookingRepo.existsByWalkerUserIdAndOwnerId(UUID.fromString(walkerUserId), UUID.fromString(clientId));
    }

//    public Booking createUberBooking(UUID clientId, Booking booking) {
//
//    }
}