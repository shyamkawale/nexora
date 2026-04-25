package com.svk.nexora_be.config;

import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.enums.UserRole;
import com.svk.nexora_be.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Initialize default ADMIN user when application starts
     * This runs after the database schema is created by Hibernate
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeDefaultAdmin() {
        try {
            // Check if admin user already exists
            String adminEmail = "admin@gmail.com";
            if (userRepository.findByEmail(adminEmail).isPresent()) {
                log.info("Admin user already exists");
                return;
            }

            User adminUser = User.builder()
                    .email(adminEmail)
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .isActive(true)
                    .role(UserRole.ADMIN)
                    .build();

            userRepository.save(adminUser);
            
            log.info("Default ADMIN user created:");
            log.info("Email: {}", adminEmail);
            log.info("Password: admin");
            
        } catch (Exception e) {
            log.error("Error initializing admin user: {}", e.getMessage(), e);
        }
    }
}
