package com.minionbase.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minionbase.auth_service.dto.RequestResetDTO;
import com.minionbase.auth_service.dto.ResetPasswordDTO;
import com.minionbase.auth_service.model.User;
import com.minionbase.auth_service.service.PasswordResetService;
import com.minionbase.auth_service.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {
    
    private final PasswordResetService passwordResetService;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public PasswordResetController(PasswordResetService passwordResetService, UserService userService,
                                        BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.passwordResetService = passwordResetService;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // Dev-friendly: returns token in response (in prod you'd email the link)
    @PostMapping("/request-reset")
    public ResponseEntity<?> requestReset(@RequestBody RequestResetDTO requestResetDTO) {
        var maybe = userService.findByUsername(requestResetDTO.username);

        if (maybe.isEmpty()) {
            return ResponseEntity.status(404).body("User not found!!");
        }

        User user = maybe.get();
        String token = passwordResetService.createTokenForUser(user.getId());
        // log token for dev visibility
        System.out.println("Password reset token for user " + user.getUsername() + " : " + token);
        // Return token in response for local dev/testing
        return ResponseEntity.ok(java.util.Map.of("resetToken", token));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        try {
            Long userId = passwordResetService.validateTokenAndGetUserId(resetPasswordDTO.token);
            User user = userService.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found!!"));
            
            // set new password hash
            user.setPasswordHash(bCryptPasswordEncoder.encode(resetPasswordDTO.newPassword));
            userService.save(user);
            
            // consume token so it can be reused
            passwordResetService.consumeToken(resetPasswordDTO.token);

            return ResponseEntity.ok(java.util.Map.of("message", "Password Updated!!"));
            
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", ex.getMessage()));
        }
    }
}
