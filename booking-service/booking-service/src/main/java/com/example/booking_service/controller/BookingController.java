package com.example.booking_service.controller;

import com.example.booking_service.client.DogServiceClient;
import com.example.booking_service.client.UserServiceClient;
import com.example.booking_service.model.Booking;
import com.example.booking_service.model.DogResponse;
import com.example.booking_service.model.OpenBooking;
import com.example.booking_service.model.OpenBookingRequest;
import com.example.booking_service.service.BookingService;
import org.springframework.http.ResponseEntity;
import com.example.booking_service.client.WalkerClient;
import com.example.booking_service.client.NotificationClient;
import com.example.booking_service.model.NotificationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

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

        // Ensure the booking owner matches the authenticated user
        if (!clientId.equals(booking.getOwnerId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Validate walker profile
        ResponseEntity<Boolean> walkerExistsResponse = walkerServiceClient.checkWalkerProfileExists(walkerUserId);
        if (!walkerExistsResponse.getBody()) {
            return ResponseEntity.badRequest().body(null);
        }

        // Validate dogs
        List<UUID> dogIds = booking.getDogIds();
        ResponseEntity<List<DogResponse>> dogsResponse = dogServiceClient.getDogsByOwner(token);
        if (!dogsResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<UUID> ownedDogIds = dogsResponse.getBody().stream()
                .map(DogResponse::getId)
                .toList();

        if (!ownedDogIds.containsAll(dogIds)) {
            return ResponseEntity.badRequest().body(null);
        }

        // Create booking
        Booking createdBooking = bookingService.createBooking(walkerUserId, clientId, booking);
        return ResponseEntity.ok(createdBooking);
    }
    // for owners that search for a faster way to book a dog walker,
    // and the dog walker is the one that accepts the booking
    @PostMapping("/open")
    public ResponseEntity<OpenBooking> createOpenBooking(
            @RequestHeader("Authorization") String token,
            @RequestBody OpenBookingRequest request
    ) {
        // Validar la autenticación del usuario
        String clientIdString = userServiceClient.getCurrentUserId(token);
        UUID clientId = UUID.fromString(clientIdString);

        // Crear la publicación de un horario abierto
        OpenBooking openBooking = bookingService.createOpenBooking(clientId, request);

        // Convertir OpenBooking a NotificationRequest
        NotificationRequest notificationRequest = mapToNotificationRequest(openBooking);

        // Notificar a los paseadores
        notificationClient.notifyWalkers(notificationRequest);

        return ResponseEntity.ok(openBooking);
    }

    private NotificationRequest mapToNotificationRequest(OpenBooking openBooking) {
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setWalkerIds(openBooking.getWalkerIds());
        notificationRequest.setMessage("New open booking available!");
        notificationRequest.setBookingId(openBooking.getId().toString());
        notificationRequest.setExpiresAt(openBooking.getExpiresAt());
        return notificationRequest;
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