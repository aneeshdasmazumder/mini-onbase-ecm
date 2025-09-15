package com.minionbase.auth_service.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minionbase.auth_service.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
}
