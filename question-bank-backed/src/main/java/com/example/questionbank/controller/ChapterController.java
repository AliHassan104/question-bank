package com.example.questionbank.controller;

import com.example.questionbank.dto.request.CreateChapterRequestDTO;
import com.example.questionbank.dto.request.UpdateChapterRequestDTO;
import com.example.questionbank.dto.response.ChapterResponseDTO;
import com.example.questionbank.service.ChapterService;
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
@RequestMapping("/api/chapters")
@Api(tags = "Chapters", description = "Chapter management operations")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Create new chapter",
            notes = "Create a new chapter (Admin/Teacher only)",
            response = ChapterResponseDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Chapter created successfully", response = ChapterResponseDTO.class),
            @ApiResponse(code = 400, message = "Invalid input or chapter name already exists in subject"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied - Admin/Teacher role required"),
            @ApiResponse(code = 404, message = "Subject not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<ChapterResponseDTO> createChapter(
            @ApiParam(value = "Chapter creation data", required = true)
            @Valid @RequestBody CreateChapterRequestDTO createChapterRequestDTO) {

        log.info("Request to create chapter: {}", createChapterRequestDTO.getName());
        ChapterResponseDTO createdChapter = chapterService.createChapter(createChapterRequestDTO);
        return new ResponseEntity<>(createdChapter, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Update chapter",
            notes = "Update an existing chapter (Admin/Teacher only)",
            response = ChapterResponseDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Chapter updated successfully", response = ChapterResponseDTO.class),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "Chapter or Subject not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<ChapterResponseDTO> updateChapter(
            @ApiParam(value = "Chapter ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "Chapter update data", required = true)
            @Valid @RequestBody UpdateChapterRequestDTO updateChapterRequestDTO) {

        log.info("Request to update chapter with ID: {}", id);
        ChapterResponseDTO updatedChapter = chapterService.updateChapter(id, updateChapterRequestDTO);
        return ResponseEntity.ok(updatedChapter);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(
            value = "Delete chapter",
            notes = "Soft delete a chapter (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Chapter deleted successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied - Admin role required"),
            @ApiResponse(code = 404, message = "Chapter not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Void> deleteChapter(
            @ApiParam(value = "Chapter ID", required = true)
            @PathVariable Long id) {

        log.info("Request to delete chapter with ID: {}", id);
        chapterService.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get chapter by ID",
            notes = "Retrieve a specific chapter by its ID",
            response = ChapterResponseDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Chapter retrieved successfully", response = ChapterResponseDTO.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Chapter not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<ChapterResponseDTO> getChapterById(
            @ApiParam(value = "Chapter ID", required = true)
            @PathVariable Long id) {

        log.debug("Request to get chapter with ID: {}", id);
        ChapterResponseDTO chapter = chapterService.getChapterById(id);
        return ResponseEntity.ok(chapter);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get all chapters",
            notes = "Retrieve all chapters without pagination",
            response = ChapterResponseDTO.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Chapters retrieved successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<List<ChapterResponseDTO>> getAllChapters() {
        log.debug("Request to get all chapters");
        List<ChapterResponseDTO> chapters = chapterService.getAllChapters();
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get all chapters with pagination",
            notes = "Retrieve all chapters with pagination support",
            response = ChapterResponseDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "Page number (0-based)", dataType = "int", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "Page size", dataType = "int", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "Sort criteria (e.g., name,asc)", dataType = "string", paramType = "query", defaultValue = "name")
    })
    public ResponseEntity<Page<ChapterResponseDTO>> getAllChaptersWithPagination(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        log.debug("Request to get all chapters with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<ChapterResponseDTO> chapters = chapterService.getAllChaptersWithPagination(pageable);
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Search chapters",
            notes = "Search chapters by name with pagination",
            response = ChapterResponseDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "Chapter name to search", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "page", value = "Page number (0-based)", dataType = "int", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "Page size", dataType = "int", paramType = "query", defaultValue = "10")
    })
    public ResponseEntity<Page<ChapterResponseDTO>> searchChapters(
            @ApiParam(value = "Chapter name to search", required = true)
            @RequestParam String name,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        log.debug("Request to search chapters with name: {}", name);
        Page<ChapterResponseDTO> chapters = chapterService.searchChapters(name, pageable);
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get chapters by subject",
            notes = "Get all active chapters for a specific subject",
            response = ChapterResponseDTO.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Chapters retrieved successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Subject not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<List<ChapterResponseDTO>> getChaptersBySubject(
            @ApiParam(value = "Subject ID", required = true)
            @PathVariable Long subjectId) {

        log.debug("Request to get chapters for subject ID: {}", subjectId);
        List<ChapterResponseDTO> chapters = chapterService.getChaptersBySubject(subjectId);
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get chapters by class",
            notes = "Get all active chapters for a specific class",
            response = ChapterResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<ChapterResponseDTO>> getChaptersByClass(
            @ApiParam(value = "Class ID", required = true)
            @PathVariable Long classId) {

        log.debug("Request to get chapters for class ID: {}", classId);
        List<ChapterResponseDTO> chapters = chapterService.getChaptersByClass(classId);
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Filter chapters",
            notes = "Filter active chapters by subject and/or class",
            response = ChapterResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<ChapterResponseDTO>> filterChapters(
            @ApiParam(value = "Subject ID (optional)")
            @RequestParam(required = false) Long subjectId,
            @ApiParam(value = "Class ID (optional)")
            @RequestParam(required = false) Long classId) {

        log.debug("Request to filter chapters with subjectId: {} and classId: {}", subjectId, classId);
        List<ChapterResponseDTO> chapters = chapterService.filterChapters(subjectId, classId);
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get all active chapters",
            notes = "Retrieve all active chapters",
            response = ChapterResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<ChapterResponseDTO>> getAllActiveChapters() {
        log.debug("Request to get all active chapters");
        List<ChapterResponseDTO> activeChapters = chapterService.getAllActiveChapters();
        return ResponseEntity.ok(activeChapters);
    }

    @GetMapping("/exists")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Check if chapter name exists in subject",
            notes = "Check if a chapter with the given name already exists in the specified subject",
            response = Boolean.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Check completed successfully", response = Boolean.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Boolean> checkChapterNameExists(
            @ApiParam(value = "Chapter name to check", required = true)
            @RequestParam String name,
            @ApiParam(value = "Subject ID", required = true)
            @RequestParam Long subjectId) {

        log.debug("Request to check if chapter name exists: {} in subject: {}", name, subjectId);
        boolean exists = chapterService.existsByNameAndSubject(name, subjectId);
        return ResponseEntity.ok(exists);
    }
}