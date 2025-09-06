package com.example.questionbank.service;

import com.example.questionbank.model.*;
import com.example.questionbank.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializationService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing database with default data...");

        try {
            // Check if data already exists
            if (userRepository.count() > 0) {
                log.info("Database already contains data. Skipping initialization.");
                return;
            }

            createDefaultPermissions();
            createDefaultRoles();
            createDefaultUsers();

            log.info("Database initialization completed successfully!");

        } catch (Exception e) {
            log.error("Error during database initialization: {}", e.getMessage(), e);
        }
    }

    private void createDefaultPermissions() {
        log.info("Creating default permissions...");

        String[] permissions = {
                "USER_READ", "USER_WRITE", "USER_DELETE",
                "QUESTION_READ", "QUESTION_WRITE", "QUESTION_DELETE",
                "SUBJECT_READ", "SUBJECT_WRITE", "SUBJECT_DELETE",
                "CHAPTER_READ", "CHAPTER_WRITE", "CHAPTER_DELETE"
        };

        for (String permName : permissions) {
            if (!permissionRepository.findByName(permName).isPresent()) {
                Permission permission = Permission.builder()
                        .name(permName)
                        .value(true)
                        .build();
                permissionRepository.save(permission);
                log.debug("Created permission: {}", permName);
            }
        }
    }

    private void createDefaultRoles() {
        log.info("Creating default roles...");

        // Create ADMIN role with all permissions
        createRoleIfNotExists("ADMIN",
                permissionRepository.findAll().toArray(new Permission[0]));

        // Create TEACHER role with limited permissions
        createRoleIfNotExists("TEACHER",
                getPermissionsByNames("USER_READ", "QUESTION_READ", "QUESTION_WRITE",
                        "QUESTION_DELETE", "SUBJECT_READ", "SUBJECT_WRITE",
                        "CHAPTER_READ", "CHAPTER_WRITE", "CHAPTER_DELETE"));

        // Create STUDENT role with read-only permissions
        createRoleIfNotExists("STUDENT",
                getPermissionsByNames("USER_READ", "QUESTION_READ", "SUBJECT_READ", "CHAPTER_READ"));
    }

    private void createRoleIfNotExists(String roleName, Permission... permissions) {
        if (!roleRepository.findByName(roleName).isPresent()) {
            Role role = Role.builder()
                    .name(roleName)
                    .permissions(Set.of(permissions))
                    .build();
            roleRepository.save(role);
            log.debug("Created role: {} with {} permissions", roleName, permissions.length);
        }
    }

    private Permission[] getPermissionsByNames(String... names) {
        return java.util.Arrays.stream(names)
                .map(name -> permissionRepository.findByName(name).orElse(null))
                .filter(java.util.Objects::nonNull)
                .toArray(Permission[]::new);
    }

    private void createDefaultUsers() {
        log.info("Creating default users...");

        // Create Admin User
        createUserIfNotExists("admin", "password123", "ADMIN");

        // Create Teacher User
        createUserIfNotExists("teacher1", "password123", "TEACHER");

        // Create Student User
        createUserIfNotExists("student1", "password123", "STUDENT");
    }

    private void createUserIfNotExists(String username, String password, String roleName) {
        if (!userRepository.findByName(username).isPresent()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

            User user = User.builder()
                    .name(username)
                    .password(passwordEncoder.encode(password))
                    .roles(Set.of(role))
                    .build();

            userRepository.save(user);
            log.info("Created user: {} with role: {}", username, roleName);
        }
    }

    /**
     * Method to clean database - USE WITH CAUTION!
     */
    @Transactional
    public void cleanDatabase() {
        log.warn("Cleaning all database content...");

        userRepository.deleteAll();
        roleRepository.deleteAll();
        permissionRepository.deleteAll();

        log.warn("Database cleaned successfully!");
    }

    /**
     * Method to reset database with fresh data
     */
    @Transactional
    public void resetDatabase() {
        log.info("Resetting database...");

        cleanDatabase();
        run(); // Re-initialize

        log.info("Database reset completed!");
    }
}