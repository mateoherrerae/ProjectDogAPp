package com.example.booking_service.service;

import com.example.booking_service.client.UserServiceClient;
import com.example.booking_service.client.WalkerClient;
import com.example.booking_service.client.NotificationClient;
import com.example.booking_service.model.*;
import com.example.booking_service.repo.BookingRepo;
import com.example.booking_service.repo.OpenBookingRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;


@Service
public class BookingService {

    // manual booking
    private final BookingRepo bookingRepo;
    // uber booking
    private final OpenBookingRepo openBookingRepo;
    private final WalkerClient walkerClient;
    private final UserServiceClient userServiceClient;
    private final NotificationClient notificationClient;


    public BookingService(BookingRepo bookingRepo, OpenBookingRepo openBookingRepo, WalkerClient walkerClient, UserServiceClient userServiceClient, NotificationClient notificationClient) {
        this.bookingRepo = bookingRepo;
        this.openBookingRepo = openBookingRepo;
        this.walkerClient = walkerClient;
        this.userServiceClient = userServiceClient;
        this.notificationClient = notificationClient;
    }

    public Booking createBooking(String token, UUID walkerUserId, UUID clientId, Booking booking) {
        // Validate walker
        ResponseEntity<Boolean> walkerValidationResponse = walkerClient.checkWalkerProfileExists(token, walkerUserId);
        if (walkerValidationResponse.getStatusCode() != HttpStatus.OK || !walkerValidationResponse.getBody()) {
            throw new IllegalArgumentException("Walker not valid or not verified");
        }

        // Validate client
        ResponseEntity<String> clientValidationResponse = userServiceClient.getUsernameById(clientId);
        if (clientValidationResponse.getStatusCode() != HttpStatus.OK || clientValidationResponse.getBody() == null) {
            throw new IllegalArgumentException("Client not valid");
        }

        // Save the booking
        booking.setWalkerUserId(walkerUserId);
        booking.setOwnerId(clientId);
        if (booking.getStatus() == null) {
            booking.setStatus(BookingStatus.PENDING);
        }
        return bookingRepo.save(booking);
    }

    public boolean bookingExists(String walkerUserId, String clientId) {
        return bookingRepo.existsByWalkerUserIdAndOwnerId(UUID.fromString(walkerUserId), UUID.fromString(clientId));
    }

    // logic of reservation like uber
    public OpenBooking createOpenBooking(UUID clientId, OpenBookingRequest request) {
        // Validate client
        ResponseEntity<String> clientValidationResponse = userServiceClient.getUsernameById(clientId);
        if (clientValidationResponse.getStatusCode() != HttpStatus.OK || clientValidationResponse.getBody() == null) {
            throw new IllegalArgumentException("Invalid client");
        }

        // Create and save the open booking
        OpenBooking openBooking = new OpenBooking();
        openBooking.setId(UUID.randomUUID());
        openBooking.setOwnerId(clientId);
        openBooking.setStartTime(request.getStartTime());
        openBooking.setEndTime(request.getEndTime());
        openBooking.setLocation(request.getLocation());
        openBooking.setAdditionalNotes(request.getAdditionalNotes());
        openBooking.setActive(true);

        // Save the booking in the database
        openBookingRepo.save(openBooking);

        // Extract lat and lng from location (assuming it's a comma-separated string "lat,lng")
        String[] coordinates = request.getLocation().split(",");
        double lat = Double.parseDouble(coordinates[0]);
        double lng = Double.parseDouble(coordinates[1]);

        // Find the nearest available walker
        ResponseEntity<List<Walker>> response = walkerClient.getNearbyWalkers(lat, lng);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null || response.getBody().isEmpty()) {
            throw new IllegalArgumentException("No nearby walkers found");
        }
        UUID nearestWalkerId = response.getBody().get(0).getId(); // Assuming the first walker is the nearest

        // Notify the walker about the booking using NotificationClient
        NotificationRequest notificationRequest = new NotificationRequest();
        List<String> walkerIds = new ArrayList<>();
        walkerIds.add(nearestWalkerId.toString());
        notificationRequest.setWalkerIds(walkerIds);
        notificationRequest.setBookingId(openBooking.getId().toString());
        notificationRequest.setMessage("You have a new booking request!");

        notificationClient.notifyWalkers(notificationRequest);

        return openBooking;
    }


}