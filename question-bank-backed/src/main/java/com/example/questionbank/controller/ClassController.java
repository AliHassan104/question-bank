package com.example.questionbank.controller;

import com.example.questionbank.dto.request.CreateClassRequestDTO;
import com.example.questionbank.dto.request.UpdateClassRequestDTO;
import com.example.questionbank.dto.response.ClassResponseDTO;
import com.example.questionbank.dto.response.ClassSummaryDTO;
import com.example.questionbank.service.ClassService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/classes")
@Api(tags = "Classes", description = "Class management operations")
public class ClassController {

    @Autowired
    private ClassService classService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Create new class",
            notes = "Create a new class (Admin/Teacher only)",
            response = ClassResponseDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Class created successfully", response = ClassResponseDTO.class),
            @ApiResponse(code = 400, message = "Invalid input or class name already exists"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied - Admin/Teacher role required"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<ClassResponseDTO> createClass(
            @ApiParam(value = "Class creation data", required = true)
            @Valid @RequestBody CreateClassRequestDTO createClassRequestDTO) {

        log.info("Request to create class with name: {}", createClassRequestDTO.getName());
        ClassResponseDTO createdClass = classService.createClass(createClassRequestDTO);
        return new ResponseEntity<>(createdClass, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Update class",
            notes = "Update an existing class (Admin/Teacher only)",
            response = ClassResponseDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Class updated successfully", response = ClassResponseDTO.class),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "Class not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<ClassResponseDTO> updateClass(
            @ApiParam(value = "Class ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "Class update data", required = true)
            @Valid @RequestBody UpdateClassRequestDTO updateClassRequestDTO) {

        log.info("Request to update class with ID: {}", id);
        ClassResponseDTO updatedClass = classService.updateClass(id, updateClassRequestDTO);
        return ResponseEntity.ok(updatedClass);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(
            value = "Delete class",
            notes = "Soft delete a class (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Class deleted successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied - Admin role required"),
            @ApiResponse(code = 404, message = "Class not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Void> deleteClass(
            @ApiParam(value = "Class ID", required = true)
            @PathVariable Long id) {

        log.info("Request to delete class with ID: {}", id);
        classService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get class by ID",
            notes = "Retrieve a specific class by its ID",
            response = ClassResponseDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Class retrieved successfully", response = ClassResponseDTO.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Class not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<ClassResponseDTO> getClassById(
            @ApiParam(value = "Class ID", required = true)
            @PathVariable Long id) {

        log.debug("Request to get class with ID: {}", id);
        ClassResponseDTO classResponse = classService.getClassById(id);
        return ResponseEntity.ok(classResponse);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get all classes",
            notes = "Retrieve all classes without pagination",
            response = ClassResponseDTO.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Classes retrieved successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<List<ClassResponseDTO>> getAllClasses() {
        log.debug("Request to get all classes");
        List<ClassResponseDTO> classes = classService.getAllClasses();
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get all classes with pagination",
            notes = "Retrieve all classes with pagination support",
            response = ClassResponseDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "Page number (0-based)", dataType = "int", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "Page size", dataType = "int", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "Sort criteria (e.g., name,asc)", dataType = "string", paramType = "query", defaultValue = "name")
    })
    public ResponseEntity<Page<ClassResponseDTO>> getAllClassesWithPagination(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        log.debug("Request to get all classes with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<ClassResponseDTO> classes = classService.getAllClassesWithPagination(pageable);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Search classes",
            notes = "Search classes by name with pagination",
            response = ClassResponseDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "Class name to search", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "page", value = "Page number (0-based)", dataType = "int", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "Page size", dataType = "int", paramType = "query", defaultValue = "10")
    })
    public ResponseEntity<Page<ClassResponseDTO>> searchClasses(
            @ApiParam(value = "Class name to search", required = true)
            @RequestParam String name,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        log.debug("Request to search classes with name: {}", name);
        Page<ClassResponseDTO> classes = classService.searchClasses(name, pageable);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get all active classes",
            notes = "Retrieve all active classes without pagination",
            response = ClassResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<ClassResponseDTO>> getAllActiveClasses() {
        log.debug("Request to get all active classes");
        List<ClassResponseDTO> activeClasses = classService.getAllActiveClasses();
        return ResponseEntity.ok(activeClasses);
    }

    @GetMapping("/active/summary")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get active classes summary",
            notes = "Retrieve summary of all active classes for dropdowns/selections",
            response = ClassSummaryDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<ClassSummaryDTO>> getAllActiveClassesSummary() {
        log.debug("Request to get all active classes summary");
        List<ClassSummaryDTO> activeClassesSummary = classService.getAllActiveClassesSummary();
        return ResponseEntity.ok(activeClassesSummary);
    }

    @GetMapping("/active/page")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get active classes with pagination",
            notes = "Retrieve all active classes with pagination support",
            response = ClassResponseDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "Page number (0-based)", dataType = "int", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "Page size", dataType = "int", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "Sort criteria", dataType = "string", paramType = "query", defaultValue = "name")
    })
    public ResponseEntity<Page<ClassResponseDTO>> getAllActiveClassesWithPagination(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        log.debug("Request to get all active classes with pagination");
        Page<ClassResponseDTO> activeClasses = classService.getAllActiveClassesWithPagination(pageable);
        return ResponseEntity.ok(activeClasses);
    }

    @GetMapping("/exists")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Check if class name exists",
            notes = "Check if a class with the given name already exists",
            response = Boolean.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Check completed successfully", response = Boolean.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Boolean> checkClassNameExists(
            @ApiParam(value = "Class name to check", required = true)
            @RequestParam String name) {

        log.debug("Request to check if class name exists: {}", name);
        boolean exists = classService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
}