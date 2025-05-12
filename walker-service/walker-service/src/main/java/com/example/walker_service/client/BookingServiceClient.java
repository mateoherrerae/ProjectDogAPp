package com.example.walker_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "booking-service")
public interface BookingServiceClient {

    @GetMapping("/api/bookings/exists")
    boolean bookingExists(@RequestParam("walkerId") String walkerId, @RequestParam("clientId") String clientId);
}
