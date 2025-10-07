package com.minionbase.auth_service.controller;

import java.util.Map;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minionbase.auth_service.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // Only Admin can list all users
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<?> listAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    // Adding roles to the User
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/users/{id}/roles")
    public ResponseEntity<?> addRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String role = body.get("role");
        userService.addRole(id, role);

        return ResponseEntity.ok().build();
    }

    // Removing roles to the User
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/users/{id}/roles/{role}")
    public ResponseEntity<?> removeRole(@PathVariable Long id, @PathVariable String role) {
        userService.removeRole(id, role);

        return ResponseEntity.noContent().build();
    }
}
