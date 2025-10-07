package com.minionbase.auth_service.dto;

public class RegisterRequest {
    public String username;
    public String password;
    @Override
    public String toString() {
        return "RegisterRequest [username=" + username + ", password=" + password + "]";
    }

    
}
