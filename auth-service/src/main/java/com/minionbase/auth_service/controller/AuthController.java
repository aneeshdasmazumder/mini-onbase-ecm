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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.minionbase.auth_service.dto.LoginRequest;
import com.minionbase.auth_service.service.TokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UserService userService;

    private final String defaultRoles;

    private final TokenService tokenService;
    public AuthController(UserService userService,
                        TokenService tokenService,
                        @Value("${app.default-roles:ROLE_USER}") String defaultRolesCsv) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.defaultRoles = defaultRolesCsv;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (req==null || req.username==null || req.password==null) return ResponseEntity.badRequest().body("Invalid");
        var userOpt = userService.findByUsername(req.username);
        if (userOpt.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
        var user = userOpt.get();
        if (!new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().matches(req.password, user.getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        String token = tokenService.generateToken(user.getUsername(), user.getRolesList());
        return ResponseEntity.ok(new java.util.HashMap<String, String>() {{ put("access_token", token); }});
    }

    // protected endpoint to return current user
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return ResponseEntity.status(401).build();
        String username = auth.getName();
        java.util.List<String> roles = auth.getAuthorities().stream().map(a->a.getAuthority()).toList();
        return ResponseEntity.ok(java.util.Map.of("username", username, "roles", roles));
    }
    
}
