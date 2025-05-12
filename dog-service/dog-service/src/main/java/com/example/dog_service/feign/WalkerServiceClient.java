package com.example.dog_service.feign;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "walker-service",
        url = "${walker-service.url}", // Usa guión, no punto
        configuration = FeignConfig.class // Para manejar seguridad
)
public class WalkerServiceClient {
}
