package com.minionbase.auth_service.exception;

public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String msg) {
        throw new RuntimeException(msg);
    }
}
