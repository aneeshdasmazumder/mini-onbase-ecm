package com.minionbase.auth_service.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minionbase.auth_service.dto.RegisterRequest;
import com.minionbase.auth_service.dto.UserResponse;
import com.minionbase.auth_service.model.User;
import com.minionbase.auth_service.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private final UserService userService;

    private final String defaultRoles;

    public AuthController(UserService userService, @Value("${app.default-roles:ROLE_USER}") String defaultRoles) {
        this.userService = userService;
        this.defaultRoles = defaultRoles;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request == null || request.username == null || request.password == null) {
            return ResponseEntity.badRequest().body("Invalid request payload");
        }

        if(userService.existsByUsername(request.username)) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        List<String> roles = List.of(defaultRoles.split(","));
        User newUser = userService.registerUser(request.username, request.password, roles);
        UserResponse response = new UserResponse();
        response.id = newUser.getId();
        response.username = newUser.getUsername();
        response.roles = newUser.getRolesList();
        return ResponseEntity.created(URI.create("/api/auth/users/" + newUser.getId())).body(response);
    }
    
    
}
