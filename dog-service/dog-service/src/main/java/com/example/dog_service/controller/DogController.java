package com.example.dog_service.controller;

import com.example.dog_service.feign.UserServiceClient;
import com.example.dog_service.model.DogRequest;
import com.example.dog_service.model.DogResponse;
import com.example.dog_service.service.DogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/dogs")
@RequiredArgsConstructor
public class DogController {

    private final DogService dogService;
    private final UserServiceClient userServiceClient;


    @PostMapping("register")
    public ResponseEntity<DogResponse> createDog(@Valid @RequestBody DogRequest request, @RequestHeader("Authorization") String token) {
        ResponseEntity<String> response = userServiceClient.getCurrentUserId(token);
        if (response.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID ownerId = UUID.fromString(response.getBody());
        DogResponse createdDog = dogService.createDog(request, ownerId);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDog.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdDog);
    }


    @GetMapping("/{dogId}")
    public ResponseEntity<DogResponse> getDogProfile(@PathVariable UUID dogId) {
        String username = obtenerUsernameDelContexto();
        boolean isOwner = username != null && dogService.isDogOwner(dogId, username);

        DogResponse response = dogService.getDogProfile(dogId, isOwner);
        return ResponseEntity.ok(response);
    }

    private String obtenerUsernameDelContexto() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String) {
            return (String) auth.getPrincipal();
        }
        return null;
    }

    //obtiene los perros del dueno en base al bearer token
    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<List<DogResponse>> getDogsByOwner(@RequestHeader("Authorization") String token) {
        ResponseEntity<String> response = userServiceClient.getCurrentUserId(token);
        if (response.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID ownerId = UUID.fromString(response.getBody());
        return ResponseEntity.ok(dogService.getDogsByOwner(ownerId));
    }

    // update the dog
    @PatchMapping("/{dogId}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<DogResponse> updateDog(
            @PathVariable UUID dogId,
            @RequestBody Map<String, Object> updates,
            @RequestHeader("Authorization") String token) {
        ResponseEntity<String> response = userServiceClient.getCurrentUserId(token);
        if (response.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID ownerId = UUID.fromString(response.getBody());
        DogResponse updatedDog = dogService.updateDog(dogId, updates, ownerId);
        return ResponseEntity.ok(updatedDog);
    }




}