package com.example.dog_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


import java.util.UUID;
@FeignClient(
        name = "user-service",
        url = "${user-service.url}", // Usa guión, no punto
        configuration = FeignConfig.class // Para manejar seguridad
)
public interface UserServiceClient {

    @GetMapping("/api/users/{userId}/username")
    ResponseEntity<String> getUsernameById(@PathVariable("userId") UUID userId);

    @GetMapping("/api/users/username/{username}/id")
    ResponseEntity<UUID> getUserIdByUsername(@PathVariable("username") String username);

    @GetMapping("/api/users/me")
    ResponseEntity<String> getCurrentUserId(@RequestHeader("Authorization") String token);


}