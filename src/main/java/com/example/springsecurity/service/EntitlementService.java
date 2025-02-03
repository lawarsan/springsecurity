package com.example.springsecurity.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EntitlementService {
    private final Map<String, List<String>> userRoles = Map.of(
            "alice", List.of("ADMIN"),
            "bob", List.of("USER"),
            "charlie", List.of("MANAGER")
    );

    private final Map<String, List<String>> rolePermissions = Map.of(
            "ADMIN", List.of("VIEW_USERS", "CREATE_USER", "DELETE_USER", "UPDATE_USER"),
            "USER", List.of("VIEW_USERS"),
            "MANAGER", List.of("VIEW_USERS", "UPDATE_USER")
    );

    public boolean hasPermission(String username, List<String> requiredPermissions) {
        List<String> roles = userRoles.getOrDefault(username, List.of());
        for (String role : roles) {
            List<String> permissions = rolePermissions.getOrDefault(role, List.of());
            for (String requiredPermission : requiredPermissions) {
                if (permissions.contains(requiredPermission)) {
                    return true;
                }
            }
        }
        return false;
    }
}
