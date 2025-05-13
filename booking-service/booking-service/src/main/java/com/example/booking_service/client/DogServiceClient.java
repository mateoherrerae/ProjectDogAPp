package com.example.booking_service.client;

import com.example.booking_service.model.DogResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "dog-service",
        url = "${dog-service.url}",
        configuration = FeignConfig.class
)
public interface DogServiceClient {

    @GetMapping("/api/dogs/owner")
    ResponseEntity<List<DogResponse>> getDogsByOwner(@RequestHeader("Authorization") String token);
}
