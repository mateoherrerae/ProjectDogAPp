package com.example.user_service.model;

import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class UserResponse {
    private UUID id;
    private String username;
    private String email;        // Solo visible para dueño/admin
    private String phone;        // Solo visible para dueño/admin
    private List<String> roles;  // Solo visible para dueño/admin
    private boolean isProfilePublic;

    // Constructor para dueño/admin
    public UserResponse(Users user, boolean isOwnerOrAdmin) {
        this.id = isOwnerOrAdmin ? user.getId() : null;
        this.username = user.getUsername();
        this.email = isOwnerOrAdmin ? user.getEmail() : "🔒";
        this.phone = isOwnerOrAdmin ? user.getPhone() : "🔒";
        this.roles = isOwnerOrAdmin
                ? user.getRoles().stream().map(Role::getName).toList()
                : List.of("🔒");
        this.isProfilePublic = user.isProfilePublic();

        // Eliminado dogsCount hasta implementar Feign con el microservicio de dogs
    }

    // Constructor para mensajes de error
    public UserResponse(String message) {
        this.username = message;
    }
}