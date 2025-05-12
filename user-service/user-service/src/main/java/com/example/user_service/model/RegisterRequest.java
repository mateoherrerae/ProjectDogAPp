package com.example.user_service.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String username;
    private String password;
    private String email;
    private String phone;
    private List<RoleType> roles; // Roles enviados desde el frontend
    private String address;

    // Getters y Setters
}
