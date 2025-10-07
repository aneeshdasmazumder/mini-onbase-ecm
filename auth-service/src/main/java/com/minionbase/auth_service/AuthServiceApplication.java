package com.minionbase.auth_service;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.minionbase.auth_service.model.User;
import com.minionbase.auth_service.respository.UserRepository;

@SpringBootApplication(scanBasePackages = "com.minionbase")
@EnableMethodSecurity(prePostEnabled = true) // enable @PreAuthorize
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

	// @Bean
	// CommandLineRunner seed(UserRepository repo, BCryptPasswordEncoder enc) {
	// 	return args -> {
	// 		if (repo.findByUsername("admin").isEmpty()) {
	// 			User u = new User("admin", enc.encode("Admin@123"), List.of("ROLE_ADMIN","ROLE_USER"));
	// 			repo.save(u);
	// 		}
	// 	};
	// }
}
