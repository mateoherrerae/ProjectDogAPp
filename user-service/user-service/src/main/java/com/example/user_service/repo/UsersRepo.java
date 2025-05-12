package com.example.user_service.repo;

import com.example.user_service.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface UsersRepo extends JpaRepository<Users, UUID> {
    Optional<Users> findByUsername(String username); // Usar Optional para manejar nulls

}