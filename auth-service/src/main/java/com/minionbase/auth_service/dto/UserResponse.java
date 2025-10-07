package com.minionbase.auth_service.dto;

import java.util.List;

public class UserResponse {
    public Long id;
    public String username;
    public List<String> roles;
    @Override
    public String toString() {
        return "UserResponse [id=" + id + ", username=" + username + ", roles=" + roles + "]";
    }

    
}
