package com.example.user_service.service;

import com.example.user_service.exception.UnauthorizedRoleAssignmentException;
import com.example.user_service.model.Role;
import com.example.user_service.model.RoleType;
import com.example.user_service.model.Users;
import com.example.user_service.repo.RoleRepository;
import com.example.user_service.repo.UsersRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    private UsersRepo usersRepository;

    // Cargar roles iniciales al iniciar la app (si no existen)
    @PostConstruct
    public void initRoles() {
        for (RoleType roleType : RoleType.values()) {
            if (!roleRepository.existsByName(roleType.name())) {
                Role role = new Role();
                role.setName(roleType.name());
                roleRepository.save(role);
            }
        }
    }
    public void assignRolesToUser(Users user, List<RoleType> roles, Users currentAdmin) {
        if (!currentAdmin.getRoles().stream().anyMatch(r -> r.getName().equals(RoleType.ROLE_ADMIN.name()))) {
            throw new UnauthorizedRoleAssignmentException("Solo ADMIN puede asignar roles");
        }

        Set<Role> validRoles = roles.stream()
                .filter(role -> role != RoleType.ROLE_ADMIN) // Evitar auto-asignación de ADMIN
                .map(roleType -> {
                    try {
                        return roleRepository.findByName(roleType.name())
                                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleType.name()));
                    } catch (RoleNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());

        user.setRoles(validRoles);
        usersRepository.save(user);
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + name));
    }
}
