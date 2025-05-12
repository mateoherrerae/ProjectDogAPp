package com.example.user_service.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
public class Users {

    @Id
    @UuidGenerator // Genera UUIDs automáticamente
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // Debe encriptarse con BCrypt antes de guardar

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    @ManyToMany(fetch = FetchType.EAGER) // Cargar roles automáticamente
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();


    @Column(nullable = false)
    private boolean isProfilePublic = false;

    @Column(nullable = false)
    private boolean walkerRequestPending = false;

    @Column(nullable = false)
    private boolean isWalkerVerified = false;



    // Nuevo campo para el domicilio
    private String address;
}
