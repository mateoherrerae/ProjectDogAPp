package com.example.walker_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(
        name = "user-service",
        url = "${user.service.url}",
        configuration = FeignConfig.class
)
public interface UserServiceClient {

    @GetMapping("/api/users/{userId}/username")
    ResponseEntity<String> getUsernameById(@PathVariable("userId") UUID userId);

    @GetMapping("api/users/me")
    // devolucion de id en base al token
    String getCurrentUserId(@RequestHeader("Authorization") String token);

    @PostMapping("/api/users/request-walker")
    ResponseEntity<String> requestWalkerRole(@RequestHeader("Authorization") String token);

}