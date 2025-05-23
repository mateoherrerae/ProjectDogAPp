package com.example.user_service.controller;

import com.example.user_service.model.Role;
import com.example.user_service.model.RoleType;
import com.example.user_service.model.Users;
import com.example.user_service.service.RoleService;
import com.example.user_service.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final UsersService usersService;
    private final RoleService roleService;

    public AdminController(UsersService usersService, RoleService roleService) {
        this.usersService = usersService;
        this.roleService = roleService;
    }

    @PostMapping("/approve-walker/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<String> approveWalkerRole(
            @PathVariable UUID userId,
            Authentication authentication) {
        log.debug("Authenticated user roles: {}", authentication.getAuthorities());
        String adminUsername = authentication.getName();
        Users adminUser = usersService.findUserByUsername(adminUsername); // Buscar por username

        if (adminUser == null) {
            throw new IllegalArgumentException("Admin user not found");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User does not have ADMIN role");
        }

        Users user = usersService.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!user.isWalkerRequestPending()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Walker request is not pending");
        }

        Role walkerRole = roleService.getRoleByName(RoleType.ROLE_WALKER.name());

        if (!user.getRoles().contains(walkerRole)) {
            user.getRoles().add(walkerRole);
            user.setWalkerRequestPending(false);
            user.setWalkerVerified(true);
            usersService.saveUser(user);
            log.info("Admin {} approved WALKER role for user {}", adminUser.getUsername(), user.getId());
            return ResponseEntity.ok("User approved as WALKER by " + adminUser.getUsername());
        } else {
            log.warn("User {} already has WALKER role", user.getId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already has WALKER role");
        }
    }

    @PostMapping("/reject-walker/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<String> rejectWalkerRole(
            @PathVariable UUID userId,
            Authentication authentication) {
        log.debug("Authenticated user roles: {}", authentication.getAuthorities());
        String adminUsername = authentication.getName();
        Users adminUser = usersService.findUserByUsername(adminUsername); // Buscar por username

        if (adminUser == null) {
            throw new IllegalArgumentException("Admin user not found");
        }

        Users user = usersService.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        user.setWalkerRequestPending(false);
        user.setWalkerVerified(false);
        usersService.saveUser(user);
        log.info("Admin {} rejected WALKER request for user {}", adminUser.getUsername(), user.getId());
        return ResponseEntity.ok("WALKER request rejected by " + adminUser.getUsername());
    }

    @PostMapping("/grant-admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<String> grantAdminRole(
            @PathVariable UUID userId,
            Authentication authentication
    ) {
        String adminUsername = authentication.getName();
        Users adminUser = usersService.findUserByUsername(adminUsername); // Buscar por username

        if (adminUser == null) {
            throw new IllegalArgumentException("Admin user not found");
        }

        Users user = usersService.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (user.getId().equals(adminUser.getId())) {
            throw new IllegalArgumentException("Admin cannot grant themselves roles");
        }

        Role adminRole = roleService.getRoleByName(RoleType.ROLE_ADMIN.name());
        user.getRoles().add(adminRole);
        usersService.saveUser(user);
        log.info("Admin {} granted ADMIN role to user {}", adminUser.getUsername(), user.getId());
        return ResponseEntity.ok("ADMIN role granted by " + adminUser.getUsername());
    }
}