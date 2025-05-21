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
    public OpenBooking createOpenBooking(UUID clientId, OpenBookingRequest request, List<UUID> nearbyWalkerIds) {
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

        // Notify the walkers about the booking using NotificationClient
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setWalkerIds(nearbyWalkerIds.stream().map(UUID::toString).toList());
        notificationRequest.setBookingId(openBooking.getId().toString());
        notificationRequest.setMessage("You have a new booking request!");

        notificationClient.notifyWalkers(notificationRequest);

        return openBooking;
    }

    // get client location by UserID
    public GeoPoint getClientLocation(UUID clientId, OpenBookingRequest request) {
        // Check if the location is provided in the request
        if (request.getLocation() != null) {
            String[] coordinates = request.getLocation().split(",");
            if (coordinates.length == 2) {
                try {
                    double lat = Double.parseDouble(coordinates[0].trim());
                    double lng = Double.parseDouble(coordinates[1].trim());
                    return new GeoPoint(lat, lng);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid location format. Expected 'lat,lng'.");
                }
            } else {
                throw new IllegalArgumentException("Invalid location format. Expected 'lat,lng'.");
            }
        }
        // Fetch the location from the user's profile via the endpoint in user ms
        ResponseEntity<GeoPoint> locationResponse = userServiceClient.getUserLocation(clientId);
        if (locationResponse.getStatusCode().is2xxSuccessful() && locationResponse.getBody() != null) {
            return locationResponse.getBody();
        }
        // Return null if location cannot be determined
        return null;
    }


}