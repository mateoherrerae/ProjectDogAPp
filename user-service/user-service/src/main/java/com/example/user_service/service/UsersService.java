package com.example.user_service.service;


import com.example.user_service.model.Role;
import com.example.user_service.model.RoleType;
import com.example.user_service.model.UserResponse;
import com.example.user_service.model.Users;
import com.example.user_service.repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;


@Service
public class UsersService {

    private final UsersRepo userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersService(UsersRepo userRepository,
                        RoleService roleService,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    public Users registerUser(Users user, List<RoleType> roles) {
        // 1. Check for null or empty password
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        // 2. Encrypt the password before any operation
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 3. Filter out disallowed roles
        List<RoleType> allowedRoles = roles.stream()
                .filter(role -> role == RoleType.ROLE_OWNER)
                .toList();

        // 4. Assign default role if necessary
        if (allowedRoles.isEmpty()) {
            allowedRoles = List.of(RoleType.ROLE_OWNER);
        }

        // 5. Initialize user roles if null
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        // 6. Assign roles to the user
        allowedRoles.forEach(role -> {
            Role roleEntity = roleService.getRoleByName(role.name());
            user.getRoles().add(roleEntity);
        });

        // 7. Save the user with the encrypted password
        return userRepository.save(user);
    }

    public Users findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    public Users findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    public Users saveUser(Users user) {
        // Validación opcional: Asegurarse de no re-encriptar la contraseña si ya está encriptada
        if (user.getPassword().startsWith("$2a$")) { // Verificar si ya está encriptada
            return userRepository.save(user);
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        }
    }

    public boolean isUserAllowedToRegisterAsWalker(UUID userId) {
        Users user = findUserById(userId);
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("WALKER"));
    }

    public void updateUserPassword(String username, String newPassword) {
        Users user = findUserByUsername(username);
        user.setPassword(passwordEncoder.encode(newPassword));
        saveUser(user);
    }

//    public UserResponse getPublicUserProfile(String username) {
//        Users user = findUserByUsername(username);
//        if (!user.isProfilePublic()) {
//            throw new PrivateProfileException(username);
//        }
//        return new UserResponse(user);
//    }
}