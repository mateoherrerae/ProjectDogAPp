package com.example.booking_service.controller;

import com.example.booking_service.client.DogServiceClient;
import com.example.booking_service.client.UserServiceClient;
import com.example.booking_service.model.*;
import com.example.booking_service.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import com.example.booking_service.client.WalkerClient;
import com.example.booking_service.client.NotificationClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import com.example.booking_service.model.GeoPoint;


@RestController
@RequestMapping("api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserServiceClient userServiceClient;
    private final WalkerClient walkerServiceClient;
    private final DogServiceClient dogServiceClient;
    private final NotificationClient notificationClient;


    public BookingController(BookingService bookingService, UserServiceClient userServiceClient,
                             WalkerClient walkerServiceClient, DogServiceClient dogServiceClient,
                             NotificationClient notificationClient) {
        this.bookingService = bookingService;
        this.userServiceClient = userServiceClient;
        this.walkerServiceClient = walkerServiceClient;
        this.dogServiceClient = dogServiceClient;
        this.notificationClient = notificationClient;
    }



    // Endpoint to create a MANUAL BOOKING
    @PostMapping("/createManual")
    public ResponseEntity<Booking> createBooking(
            @RequestHeader("Authorization") String token,
            @RequestParam("walkerUserId") UUID walkerUserId,
            @RequestBody Booking booking
    ) {
        // Validate client
        String clientIdString = userServiceClient.getCurrentUserId(token);
        UUID clientId = UUID.fromString(clientIdString);

        if (!clientId.equals(booking.getOwnerId())) {
            // Return forbidden if clientId does not match booking.ownerId
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Validate walker
        ResponseEntity<Boolean> walkerExistsResponse = walkerServiceClient.checkWalkerProfileExists(token, walkerUserId);
        if (walkerExistsResponse.getStatusCode() != HttpStatus.OK || Boolean.FALSE.equals(walkerExistsResponse.getBody())) {
            // Return bad request if walker is invalid
            return ResponseEntity.badRequest().body(null);
        }

        // Validate dogs
        List<UUID> dogIds = booking.getDogIds();
        ResponseEntity<List<DogResponse>> dogsResponse = dogServiceClient.getDogsByOwner(token);
        if (!dogsResponse.getStatusCode().is2xxSuccessful()) {
            // Return bad request if DogService does not respond successfully
            return ResponseEntity.badRequest().body(null);
        }

        List<UUID> ownedDogIds = dogsResponse.getBody().stream()
                .map(DogResponse::getId)
                .toList();
        if (!ownedDogIds.containsAll(dogIds)) {
            // Return bad request if dogs do not belong to the client
            return ResponseEntity.badRequest().body(null);
        }

        // Validate meetingPoint
        if (booking.getMeetingPoint() == null || booking.getMeetingPoint().isEmpty()) {
            // Return bad request if meetingPoint is missing
            return ResponseEntity.badRequest().body(null);
        }

        // Set walkerId and walkerUserId in the booking object
        booking.setWalkerId(walkerUserId);
        booking.setWalkerUserId(walkerUserId);

        // Create booking
        Booking createdBooking = bookingService.createBooking(token, walkerUserId, clientId, booking);
        return ResponseEntity.ok(createdBooking);
    }





    // for owners that search for a faster way to book a dog walker,
    // and the dog walker is the one that accepts the booking
    @PostMapping("/open")
    public ResponseEntity<OpenBooking> createOpenBooking(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody OpenBookingRequest request
    ) {
        // validate client
        String clientIdString = userServiceClient.getCurrentUserId(token);
        UUID clientId = UUID.fromString(clientIdString);
        // Get client location (from request or profile)
        GeoPoint location = bookingService.getClientLocation(clientId, request);
        if (location == null) {
            // Handle case where location is not available
            return ResponseEntity.badRequest().body(null);
        }

        // search walkers in a radius of 6km we can change this radius in FUTURE CREATE ENDPOINT TO CHANGE RADIUS
        List<Walker> nearbyWalkers;
        ResponseEntity<List<Walker>> response = walkerServiceClient.getNearbyWalkers(location.getLat(), location.getLng());
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            nearbyWalkers = response.getBody();
        } else {
            // handle case where no walkers are found or the response is invalid
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        if (nearbyWalkers.isEmpty()) {
            // handle case where no walkers are nearby
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // 4. Crear open booking
        OpenBooking openBooking = bookingService.createOpenBooking(
                clientId,
                request,
                nearbyWalkers.stream().map(Walker::getId).toList()
        );

        // 5. Notificar walkers (asíncrono)
        notificationClient.notifyWalkersAsync(
                new NotificationRequest(
                        nearbyWalkers,
                        "Nueva reserva disponible en tu área!",
                        openBooking.getExpiresAt()
                )
        );

        return ResponseEntity.ok(openBooking);
    }







//    // Endpoint to create a booking
//    @PostMapping("/create")
//    public ResponseEntity<Booking> createBooking(
//            @RequestParam("walkerUserId") UUID walkerUserId,
//            @RequestParam("clientId") UUID clientId,
//            @RequestBody Booking booking
//    ) {
//        Booking createdBooking = bookingService.createBooking(walkerUserId, clientId, booking);
//        return ResponseEntity.ok(createdBooking);
//    }
//
//
//
//    // Endpoint to check if a booking exists
//    @GetMapping("/exists")
//    public ResponseEntity<Boolean> bookingExists(
//            @RequestParam("walkerUserId") String walkerUserId,
//            @RequestParam("clientId") String clientId
//    ) {
//        boolean exists = bookingService.bookingExists(walkerUserId, clientId);
//        return ResponseEntity.ok(exists);
//    }



}