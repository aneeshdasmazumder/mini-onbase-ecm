package com.minionbase.auth_service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.minionbase.auth_service.model.User;
import com.minionbase.auth_service.respository.UserRepository;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // Check if username exists
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    // Register a new user with hashed password
    public User registerUser(String username, String rawPassword, java.util.List<String> roles) {
        if (existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists!!");
        }
        String hashedPassword = passwordEncoder.encode(rawPassword);
        User newUser = new User(username, hashedPassword, roles);
        return userRepository.save(newUser);
    }

    // Find user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
