package com.example.booking_service.client;

import com.example.booking_service.model.Walker;
import com.example.booking_service.model.WalkerDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "walker-service", url = "${walker-service.url}")
public interface WalkerClient {

    // Validar si el paseador existe y está verificado
    @GetMapping("/api/walkers/exists/{userId}")
    ResponseEntity<Boolean> checkWalkerProfileExists(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID userId
    );


    // obtener datos del paseador con userId
    @GetMapping("/api/walkers/{id}")
    public ResponseEntity<WalkerDetailsResponse> getWalkerById(@PathVariable("id") String id);


    @GetMapping("/api/walkers/nearby")
    ResponseEntity<List<Walker>> getNearbyWalkers(@RequestParam("lat") double lat, @RequestParam("lng") double lng);
}