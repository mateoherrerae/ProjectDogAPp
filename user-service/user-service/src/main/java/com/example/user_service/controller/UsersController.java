package com.example.user_service.controller;


import com.example.user_service.model.*;
import com.example.user_service.service.JwtService;
import com.example.user_service.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService usersService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersController(UsersService usersService, JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.usersService = usersService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    // el registro consta de un username, password, email, phone, address y rol no hace falta ya q viene asignado por defecto el de owner
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (usersService.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El username ya está en uso.");
        }
        if (usersService.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya está en uso.");
        }
        if (usersService.existsByPhone(request.getPhone())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El número de teléfono ya está en uso.");
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        List<RoleType> roles = request.getRoles() != null ? request.getRoles() : List.of(RoleType.ROLE_OWNER);
        Users savedUser = usersService.registerUser(user, roles);

        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // autentica usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // se obtiene usuario desde la base de datos
        Users user = usersService.findUserByUsername(request.getUsername());

        // se genera el token con toda la info del usuario
        String jwt = jwtService.generateToken(user);

        return ResponseEntity.ok(Map.of("token", jwt));
    }

    // ver mis detalles por bearer token
    @GetMapping("/me/details")
    public ResponseEntity<Users> getMyDetails(Authentication authentication) {
        Users user = usersService.findUserByUsername(authentication.getName());
        return ResponseEntity.ok(user);
    }



    // Endpoint público para ver perfiles (solo si son públicos)
    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getPublicProfile(
            @PathVariable String username, Authentication authentication) {
        Users requestedUser = usersService.findUserByUsername(username);
        Users currentUser = (authentication != null)
                ? usersService.findUserByUsername(authentication.getName())
                : null;

        boolean isOwner = currentUser != null && currentUser.getUsername().equals(username);
        boolean isAdmin = currentUser != null && currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        // Reglas de visibilidad:
        if (requestedUser.isProfilePublic() || isOwner || isAdmin) {
            return ResponseEntity.ok(new UserResponse(requestedUser, isOwner || isAdmin));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new UserResponse("Este perfil es privado"));
        }
    }

    // Endpoint para cambiar la visibilidad del perfil
    @PatchMapping("/me/visibility")
    public ResponseEntity<UserResponse> updateProfileVisibility(
            @RequestBody Map<String, Boolean> request,
            Authentication authentication
    ) {
        boolean isPublic = request.get("isPublic");
        Users user = usersService.findUserByUsername(authentication.getName());
        user.setProfilePublic(isPublic);
        usersService.saveUser(user);
        return ResponseEntity.ok(new UserResponse(user, true));
    }







    // Endpoint para obtener el ID del usuario autenticado
    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUserId(Authentication authentication) {
        Users user = usersService.findUserByUsername(authentication.getName());
        return ResponseEntity.ok(user.getId().toString());
    }


    //open feign to dogs
    @GetMapping("/{userId}/username")
    public ResponseEntity<String> getUsernameById(@PathVariable UUID userId) {
        Users user = usersService.findUserById(userId);
        return ResponseEntity.ok(user.getUsername());
    }

    // Proporciona el ID del usuario a partir de su username
    @GetMapping("/username/{username}/id")
    public ResponseEntity<UUID> getUserIdByUsername(@PathVariable String username) {
        Users user = usersService.findUserByUsername(username);
        return ResponseEntity.ok(user.getId());
    }



    // feign to walker,
    // una vez registrado el walker se pone en True el campo walkerRequestPending
    // para que luego el admin lo habilite como walker, y le concede el role de walker.
    @PostMapping("/request-walker")
    public ResponseEntity<String> requestWalkerRole(@RequestHeader("Authorization") String token) {
        // Extract username from JWT token
        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
        Users user = usersService.findUserByUsername(username);
        user.setWalkerRequestPending(true);
        usersService.saveUser(user);
        return ResponseEntity.ok("Solicitud para ser WALKER enviada.");
    }

    @GetMapping("/validateWalker")
    public ResponseEntity<Boolean> validateWalker(Authentication authentication) {
        Users user = usersService.findUserByUsername(authentication.getName());
        boolean isWalker = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("WALKER"));
        return ResponseEntity.ok(isWalker);
    }


    // endpoint to the ms of booking to obtain the lat and lng of the user, and then pass it to nearby(walker ms)
    @GetMapping("/{userId}/location")
    public ResponseEntity<GeoPoint> getUserLocation(@PathVariable UUID userId) {
        Users user = usersService.findUserById(userId);

        // Parse the address to extract lat and lng (mocked here)
        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
            String[] addressParts = user.getAddress().split(","); // Assuming "lat,lng" format
            if (addressParts.length == 2) {
                try {
                    double lat = Double.parseDouble(addressParts[0].trim());
                    double lng = Double.parseDouble(addressParts[1].trim());
                    return ResponseEntity.ok(new GeoPoint(lat, lng));
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(null);
                }
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    // endpoint to update the user password
    @PatchMapping("/me/password")
    public ResponseEntity<String> updatePassword(
            @RequestBody Map<String, String> request,
            Authentication authentication
    ) {
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (currentPassword == null || currentPassword.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("Las contraseñas no pueden estar vacías.");
        }

        Users user = usersService.findUserByUsername(authentication.getName());

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("La contraseña actual es incorrecta.");
        }

        // Update to the new password
        usersService.updateUserPassword(user.getUsername(), newPassword);

        return ResponseEntity.ok("Contraseña actualizada correctamente.");
    }



    // endpoint to update the user
    @PatchMapping("/me/profile")
    public ResponseEntity<Users> updateProfile(
            @RequestBody Map<String, Object> updates,
            Authentication authentication
    ) {
        Users user = usersService.findUserByUsername(authentication.getName());

        updates.forEach((key, value) -> {
            switch (key) {
                case "username":
                    String newUsername = value.toString();
                    if (usersService.existsByUsername(newUsername)) {
                        throw new IllegalArgumentException("El username ya está en uso.");
                    }
                    if (user.getLastUsernameChange() != null &&
                            user.getLastUsernameChange().plusDays(30).isAfter(LocalDateTime.now())) {
                        throw new IllegalArgumentException("Solo puedes cambiar tu username cada 30 días.");
                    }
                    user.setUsername(newUsername);
                    user.setLastUsernameChange(LocalDateTime.now());
                    break;
                case "email":
                    String newEmail = value.toString();
                    if (!newEmail.equals(user.getEmail()) && usersService.existsByEmail(newEmail)) {
                        throw new IllegalArgumentException("El email ya está en uso.");
                    }
                    user.setEmail(newEmail);
                    break;
                case "phone":
                    String newPhone = value.toString();
                    if (usersService.existsByPhone(newPhone)) {
                        throw new IllegalArgumentException("El número de teléfono ya está en uso.");
                    }
                    user.setPhone(newPhone);
                    break;
                case "address":
                    user.setAddress(value.toString());
                    break;
                default:
                    throw new IllegalArgumentException("Campo no válido: " + key);
            }
        });

        Users updatedUser = usersService.saveUser(user);
        return ResponseEntity.ok(updatedUser);
    }

}
