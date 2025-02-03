package com.example.springsecurity.filters;

import com.example.springsecurity.service.EntitlementService;
import com.example.springsecurity.service.PermissionMappingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class EntitlementFilter extends OncePerRequestFilter {
    private final EntitlementService entitlementService;
    private final PermissionMappingService permissionMappingService;

    public EntitlementFilter(EntitlementService entitlementService, PermissionMappingService permissionMappingService) {
        this.entitlementService = entitlementService;
        this.permissionMappingService = permissionMappingService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String user = "alice";//request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        String method = request.getMethod();
        String path = request.getRequestURI();
        var requiredPermission = List.of(permissionMappingService.getRequiredPermission(method, path));

        if (requiredPermission != null && !entitlementService.hasPermission(user, requiredPermission)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        System.out.println("User: " + user);
        System.out.println("Requested Path: " + path);
        System.out.println("Required Permission: " + requiredPermission);
        System.out.println("Has Permission: " + entitlementService.hasPermission(user, requiredPermission));

        System.out.println("Before filterChain.doFilter() call");
        filterChain.doFilter(request, response);
        System.out.println("After filterChain.doFilter() call");
    }
}

