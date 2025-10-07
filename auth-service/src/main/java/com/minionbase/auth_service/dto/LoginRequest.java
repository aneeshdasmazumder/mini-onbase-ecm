package com.minionbase.auth_service.dto;

public class LoginRequest { public String username; public String password;
@Override
public String toString() {
    return "LoginRequest [username=" + username + ", password=" + password + "]";
} }
