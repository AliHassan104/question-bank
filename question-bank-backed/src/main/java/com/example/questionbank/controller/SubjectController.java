package com.example.questionbank.controller;

import com.example.questionbank.dto.request.CreateSubjectRequestDTO;
import com.example.questionbank.dto.request.UpdateSubjectRequestDTO;
import com.example.questionbank.dto.response.SubjectResponseDTO;
import com.example.questionbank.service.SubjectService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/subjects")
@Api(tags = "Subjects", description = "Subject management operations")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Create new subject",
            notes = "Create a new subject (Admin/Teacher only)",
            response = SubjectResponseDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Subject created successfully", response = SubjectResponseDTO.class),
            @ApiResponse(code = 400, message = "Invalid input or subject name already exists in class"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied - Admin/Teacher role required"),
            @ApiResponse(code = 404, message = "Class not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<SubjectResponseDTO> createSubject(
            @ApiParam(value = "Subject creation data", required = true)
            @Valid @RequestBody CreateSubjectRequestDTO createSubjectRequestDTO) {

        log.info("Request to create subject: {}", createSubjectRequestDTO.getName());
        SubjectResponseDTO createdSubject = subjectService.createSubject(createSubjectRequestDTO);
        return new ResponseEntity<>(createdSubject, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Update subject",
            notes = "Update an existing subject (Admin/Teacher only)",
            response = SubjectResponseDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Subject updated successfully", response = SubjectResponseDTO.class),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "Subject or Class not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<SubjectResponseDTO> updateSubject(
            @ApiParam(value = "Subject ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "Subject update data", required = true)
            @Valid @RequestBody UpdateSubjectRequestDTO updateSubjectRequestDTO) {

        log.info("Request to update subject with ID: {}", id);
        SubjectResponseDTO updatedSubject = subjectService.updateSubject(id, updateSubjectRequestDTO);
        return ResponseEntity.ok(updatedSubject);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(
            value = "Delete subject",
            notes = "Soft delete a subject (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Subject deleted successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied - Admin role required"),
            @ApiResponse(code = 404, message = "Subject not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Void> deleteSubject(
            @ApiParam(value = "Subject ID", required = true)
            @PathVariable Long id) {

        log.info("Request to delete subject with ID: {}", id);
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get subject by ID",
            notes = "Retrieve a specific subject by its ID",
            response = SubjectResponseDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Subject retrieved successfully", response = SubjectResponseDTO.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Subject not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<SubjectResponseDTO> getSubjectById(
            @ApiParam(value = "Subject ID", required = true)
            @PathVariable Long id) {

        log.debug("Request to get subject with ID: {}", id);
        SubjectResponseDTO subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(subject);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get all subjects",
            notes = "Retrieve all subjects without pagination",
            response = SubjectResponseDTO.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Subjects retrieved successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<List<SubjectResponseDTO>> getAllSubjects() {
        log.debug("Request to get all subjects");
        List<SubjectResponseDTO> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get all subjects with pagination",
            notes = "Retrieve all subjects with pagination support",
            response = SubjectResponseDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "Page number (0-based)", dataType = "int", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "Page size", dataType = "int", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "Sort criteria (e.g., name,asc)", dataType = "string", paramType = "query", defaultValue = "name")
    })
    public ResponseEntity<Page<SubjectResponseDTO>> getAllSubjectsWithPagination(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        log.debug("Request to get all subjects with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<SubjectResponseDTO> subjects = subjectService.getAllSubjectsWithPagination(pageable);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Search subjects",
            notes = "Search subjects by name with pagination",
            response = SubjectResponseDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "Subject name to search", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "page", value = "Page number (0-based)", dataType = "int", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "Page size", dataType = "int", paramType = "query", defaultValue = "10")
    })
    public ResponseEntity<Page<SubjectResponseDTO>> searchSubjects(
            @ApiParam(value = "Subject name to search", required = true)
            @RequestParam String name,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        log.debug("Request to search subjects with name: {}", name);
        Page<SubjectResponseDTO> subjects = subjectService.searchSubjects(name, pageable);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get subjects by class",
            notes = "Get all active subjects for a specific class",
            response = SubjectResponseDTO.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Subjects retrieved successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Class not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<List<SubjectResponseDTO>> getSubjectsByClass(
            @ApiParam(value = "Class ID", required = true)
            @PathVariable Long classId) {

        log.debug("Request to get subjects for class ID: {}", classId);
        List<SubjectResponseDTO> subjects = subjectService.getSubjectsByClass(classId);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get all active subjects",
            notes = "Retrieve all active subjects",
            response = SubjectResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<SubjectResponseDTO>> getAllActiveSubjects() {
        log.debug("Request to get all active subjects");
        List<SubjectResponseDTO> activeSubjects = subjectService.getAllActiveSubjects();
        return ResponseEntity.ok(activeSubjects);
    }

    @GetMapping("/exists")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Check if subject name exists in class",
            notes = "Check if a subject with the given name already exists in the specified class",
            response = Boolean.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Check completed successfully", response = Boolean.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Boolean> checkSubjectNameExists(
            @ApiParam(value = "Subject name to check", required = true)
            @RequestParam String name,
            @ApiParam(value = "Class ID", required = true)
            @RequestParam Long classId) {

        log.debug("Request to check if subject name exists: {} in class: {}", name, classId);
        boolean exists = subjectService.existsByNameAndClass(name, classId);
        return ResponseEntity.ok(exists);
    }
}