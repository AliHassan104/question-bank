package com.example.questionbank.controller;

import com.example.questionbank.service.DatabaseInitializationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/database")
@RequiredArgsConstructor
@Api(tags = "Database Administration")
public class DatabaseAdminController {

    private final DatabaseInitializationService databaseService;

    @PostMapping("/reset")
    @ApiOperation("Reset database with fresh data - ADMIN ONLY")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> resetDatabase() {
        try {
            databaseService.resetDatabase();
            return ResponseEntity.ok("Database reset successfully with default data!");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error resetting database: " + e.getMessage());
        }
    }

    @PostMapping("/clean")
    @ApiOperation("Clean all database content - ADMIN ONLY")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cleanDatabase() {
        try {
            databaseService.cleanDatabase();
            return ResponseEntity.ok("Database cleaned successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error cleaning database: " + e.getMessage());
        }
    }

    @PostMapping("/initialize")
    @ApiOperation("Initialize database with default data - ADMIN ONLY")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> initializeDatabase() {
        try {
            databaseService.run();
            return ResponseEntity.ok("Database initialized successfully with default data!");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error initializing database: " + e.getMessage());
        }
    }
}