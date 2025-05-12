package com.example.booking_service.client;

import com.example.booking_service.model.WalkerDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "walker-service", url = "${walker-service.url}")
public interface WalkerClient {

    // Validar si el paseador existe y está verificado
    @GetMapping("/api/walker/exists/{userId}")
    ResponseEntity<Boolean> checkWalkerProfileExists(@PathVariable UUID userId);


    // obtener datos del paseador con userId
    @GetMapping("/api/walkers/{id}")
    public ResponseEntity<WalkerDetailsResponse> getWalkerById(@PathVariable("id") String id);

}
