package com.example.booking_service.controller;

import com.example.booking_service.model.Booking;
import com.example.booking_service.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Endpoint to create a booking
    @PostMapping("/create")
    public ResponseEntity<Booking> createBooking(
            @RequestParam("walkerUserId") UUID walkerUserId,
            @RequestParam("clientId") UUID clientId,
            @RequestBody Booking booking
    ) {
        Booking createdBooking = bookingService.createBooking(walkerUserId, clientId, booking);
        return ResponseEntity.ok(createdBooking);
    }



    // Endpoint to check if a booking exists
    @GetMapping("/exists")
    public ResponseEntity<Boolean> bookingExists(
            @RequestParam("walkerUserId") String walkerUserId,
            @RequestParam("clientId") String clientId
    ) {
        boolean exists = bookingService.bookingExists(walkerUserId, clientId);
        return ResponseEntity.ok(exists);
    }

    // endpoint tipo uber para crear una reserva ve quien esta mas cerca segun la ubicacion del cliente y ubicacion del paseador
//    @PostMapping("/create-uber")
//    public ResponseEntity<Booking> createUberBooking(
//            @RequestParam("clientId") UUID clientId,
//            @RequestBody Booking booking
//    ) {
//        Booking createdBooking = bookingService.createUberBooking(clientId, booking);
//        return ResponseEntity.ok(createdBooking);
//    }

}