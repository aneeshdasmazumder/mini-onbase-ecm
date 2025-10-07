package com.minionbase.auth_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minionbase.auth_service.exception.UserNotFoundException;
import com.minionbase.auth_service.model.User;
import com.minionbase.auth_service.respository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    
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

    // Find user by username
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    // List all users
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    // Adding role to the user
    @Transactional
    public void addRole(Long userId, String role) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User Not Found!!"));

        var roles = user.getRolesList();

        if(!roles.contains(role)) {
            roles.add(role);
            user.setRolesList(roles);
            userRepository.save(user);
        }
    }

    //  Remove user roles
    @Transactional
    public void removeRole(Long userId, String role) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User Not Found!!"));
        
        var roles = user.getRolesList(); 
        if(roles.remove(role)) {
            user.setRolesList(roles);
            userRepository.save(user);
        }
    }

}
