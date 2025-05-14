package com.example.walker_service.controller;

import com.example.walker_service.client.UserServiceClient;
import com.example.walker_service.model.Walker;
import com.example.walker_service.model.WalkerDetailsResponse;
import com.example.walker_service.service.WalkerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/walkers")
public class WalkerController {

    private final WalkerService walkerService;
    private final UserServiceClient userServiceClient;

    public WalkerController(WalkerService walkerService, UserServiceClient userServiceClient) {
        this.walkerService = walkerService;
        this.userServiceClient = userServiceClient;
    }


//    POST /api/walkers/register	Crea perfil de paseador (verifica rol en User Service vía JWT).

    @PostMapping("/register")
    public ResponseEntity<Walker> registerWalker(
            @RequestBody Walker walker,
            @RequestHeader("Authorization") String authHeader
    ) {
        // Pass the complete Authorization header to the User Service request to be validated as a walker
        userServiceClient.requestWalkerRole(authHeader);

        // extract token for local use
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;

        // ignore review if present
        if (walker.getReviews() != null && !walker.getReviews().isEmpty()) {
            walker.setReviews(new ArrayList<>());
        }

        Walker registeredWalker = walkerService.registerWalker(walker, token);
        return ResponseEntity.ok(registeredWalker);
    }


    // ver mi info a traves de mi bearer token del login
    @GetMapping("/mydetails")
    public ResponseEntity<WalkerDetailsResponse> getWalkerDetails(@RequestHeader("Authorization") String authHeader) {
        String authenticatedUserId = userServiceClient.getCurrentUserId(authHeader);

        Walker walker = walkerService.getWalkerByUserId(authenticatedUserId);
        if (walker == null) {
            return ResponseEntity.notFound().build();
        }
        double averageRating = walkerService.calculateAverageRating(walker);

        // Convert availability from Map<String, List<LocalTime>> to Map<String, List<String>>
        Map<String, List<String>> availability = walker.getAvailability().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(LocalTime::toString)
                                .collect(Collectors.toList())
                ));

        return ResponseEntity.ok(new WalkerDetailsResponse(
                walker.getId(),
                walker.getHourlyRate(),
                walker.getServiceAreas(),
                walker.getLocation(),
                availability,
                walker.getCertifications(),
                averageRating,
                walker.getDescription()
        ));
    }

    // Endpoint to get walker details by id
    @GetMapping("/{id}")
    public ResponseEntity<WalkerDetailsResponse> getWalkerById(@PathVariable("id") String id) {
        Walker walker = walkerService.getWalkerByUserId(id);
        if (walker != null) {
            WalkerDetailsResponse response = new WalkerDetailsResponse(
                    walker.getId(),
                    walker.getHourlyRate(),
                    walker.getServiceAreas(),
                    walker.getLocation(),
                    walker.getAvailability().entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue().stream()
                                            .map(LocalTime::toString)
                                            .collect(Collectors.toList())
                            )),
                    walker.getCertifications(),
                    walkerService.calculateAverageRating(walker),
                    walker.getDescription()
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



//    GET /api/walkers/nearby?lat=X&lng=Y	Busca paseadores cerca de una ubicación (usando geospatial query).
    @GetMapping("/nearby")
    public ResponseEntity<List<Walker>> getNearbyWalkers(@RequestParam double lat, @RequestParam double lng) {
        List<Walker> nearbyWalkers = walkerService.findNearbyWalkers(lat, lng);
        return ResponseEntity.ok(nearbyWalkers);
    }

    // verify if the walker profile exists for a given userId
    @GetMapping("/exists/{userId}")
    public ResponseEntity<Boolean> checkWalkerProfileExists(@PathVariable UUID userId) {
        boolean exists = walkerService.existsByUserId(userId);
        return ResponseEntity.ok(exists);
    }

//    POST /api/walkers/{id}/reviews	Añade reseña (con validación de que el usuario haya contratado al paseador).


//    GET /api/walkers/top	Top 10 paseadores por rating (usando MongoDB Aggregation).



    // una vez creado el booking, y crear el endpoint /api/bookings/exists probar

//    @PostMapping("/{walkerId}/reviews")
//    public ResponseEntity<String> addReview(
//            @PathVariable("walkerId") String walkerId,
//            @RequestParam("clientId") String clientId,
//            @RequestBody Review review
//    ) {
//        // Verificar si existe una reserva entre el cliente y el paseador
//        boolean bookingExists = bookingServiceClient.bookingExists(walkerId, clientId);
//        if (!bookingExists) {
//            return ResponseEntity.status(403).body("No booking found between client and walker.");
//        }
//
//        // Agregar la reseña
//        Walker walker = walkerService.getWalkerByUserId(walkerId);
//        if (walker == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        review.setWalker(walker);
//        walker.getReviews().add(review);
//        walkerService.saveWalker(walker);
//
//        return ResponseEntity.ok("Review added successfully.");
//    }

}
