package com.example.questionbank.controller;

import com.example.questionbank.dto.request.CreateMCQOptionRequestDTO;
import com.example.questionbank.dto.request.UpdateMCQOptionRequestDTO;
import com.example.questionbank.dto.response.MCQOptionResponseDTO;
import com.example.questionbank.service.MCQOptionService;
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
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/mcq-options")
@Api(tags = "MCQ Options", description = "MCQ Option management operations")
public class MCQOptionController {

    @Autowired
    private MCQOptionService mcqOptionService;

    // ============ BASIC CRUD OPERATIONS ============

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Create MCQ option",
            notes = "Create a new MCQ option (Admin/Teacher only)",
            response = MCQOptionResponseDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "MCQ option created successfully", response = MCQOptionResponseDTO.class),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied - Admin/Teacher role required"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<MCQOptionResponseDTO> createMCQOption(
            @ApiParam(value = "MCQ option creation data", required = true)
            @Valid @RequestBody CreateMCQOptionRequestDTO dto) {

        log.info("Request to create MCQ option");
        MCQOptionResponseDTO createdOption = mcqOptionService.createMCQOption(dto);
        return new ResponseEntity<>(createdOption, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Update MCQ option",
            notes = "Update an existing MCQ option (Admin/Teacher only)",
            response = MCQOptionResponseDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "MCQ option updated successfully", response = MCQOptionResponseDTO.class),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "MCQ option not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<MCQOptionResponseDTO> updateMCQOption(
            @ApiParam(value = "MCQ option ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "MCQ option update data", required = true)
            @Valid @RequestBody UpdateMCQOptionRequestDTO dto) {

        log.info("Request to update MCQ option with ID: {}", id);
        MCQOptionResponseDTO updatedOption = mcqOptionService.updateMCQOption(id, dto);
        return ResponseEntity.ok(updatedOption);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(
            value = "Delete MCQ option",
            notes = "Soft delete an MCQ option (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "MCQ option deleted successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied - Admin role required"),
            @ApiResponse(code = 404, message = "MCQ option not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Void> deleteMCQOption(
            @ApiParam(value = "MCQ option ID", required = true)
            @PathVariable Long id) {

        log.info("Request to delete MCQ option with ID: {}", id);
        mcqOptionService.deleteMCQOption(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get MCQ option by ID",
            notes = "Retrieve a specific MCQ option by its ID",
            response = MCQOptionResponseDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "MCQ option retrieved successfully", response = MCQOptionResponseDTO.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "MCQ option not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<MCQOptionResponseDTO> getMCQOptionById(
            @ApiParam(value = "MCQ option ID", required = true)
            @PathVariable Long id) {

        log.debug("Request to get MCQ option with ID: {}", id);
        MCQOptionResponseDTO option = mcqOptionService.getMCQOptionById(id);
        return ResponseEntity.ok(option);
    }

    // ============ BULK OPERATIONS ============

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Create multiple MCQ options",
            notes = "Create multiple MCQ options in bulk",
            response = MCQOptionResponseDTO.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "MCQ options created successfully"),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<List<MCQOptionResponseDTO>> createMCQOptions(
            @ApiParam(value = "List of MCQ option creation data", required = true)
            @Valid @RequestBody List<CreateMCQOptionRequestDTO> dtos) {

        log.info("Request to create {} MCQ options in bulk", dtos.size());
        List<MCQOptionResponseDTO> createdOptions = mcqOptionService.createMCQOptions(dtos);
        return new ResponseEntity<>(createdOptions, HttpStatus.CREATED);
    }

    @PutMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Update multiple MCQ options",
            notes = "Update multiple MCQ options in bulk",
            response = MCQOptionResponseDTO.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "MCQ options updated successfully"),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<List<MCQOptionResponseDTO>> updateMCQOptions(
            @ApiParam(value = "List of MCQ option update data", required = true)
            @Valid @RequestBody List<UpdateMCQOptionRequestDTO> dtos) {

        log.info("Request to update {} MCQ options in bulk", dtos.size());
        List<MCQOptionResponseDTO> updatedOptions = mcqOptionService.updateMCQOptions(dtos);
        return ResponseEntity.ok(updatedOptions);
    }

    @DeleteMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(
            value = "Delete multiple MCQ options",
            notes = "Delete multiple MCQ options by IDs"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "MCQ options deleted successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Void> deleteMCQOptionsByIds(
            @ApiParam(value = "List of MCQ option IDs", required = true)
            @RequestBody List<Long> ids) {

        log.info("Request to delete {} MCQ options in bulk", ids.size());
        mcqOptionService.deleteMCQOptionsByIds(ids);
        return ResponseEntity.noContent().build();
    }

    // ============ QUESTION-BASED OPERATIONS ============

    @GetMapping("/question/{questionId}")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get options by question ID",
            notes = "Get all MCQ options for a specific question",
            response = MCQOptionResponseDTO.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "MCQ options retrieved successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Question not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<List<MCQOptionResponseDTO>> getOptionsByQuestionId(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId) {

        log.debug("Request to get MCQ options for question ID: {}", questionId);
        List<MCQOptionResponseDTO> options = mcqOptionService.getOptionsByQuestionId(questionId);
        return ResponseEntity.ok(options);
    }

    @GetMapping("/question/{questionId}/active")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get active options by question ID",
            notes = "Get all active MCQ options for a specific question",
            response = MCQOptionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<MCQOptionResponseDTO>> getActiveOptionsByQuestionId(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId) {

        log.debug("Request to get active MCQ options for question ID: {}", questionId);
        List<MCQOptionResponseDTO> options = mcqOptionService.getActiveOptionsByQuestionId(questionId);
        return ResponseEntity.ok(options);
    }

    @GetMapping("/question/{questionId}/correct")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get correct options by question ID",
            notes = "Get all correct MCQ options for a specific question",
            response = MCQOptionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<MCQOptionResponseDTO>> getCorrectOptionsByQuestionId(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId) {

        log.debug("Request to get correct MCQ options for question ID: {}", questionId);
        List<MCQOptionResponseDTO> options = mcqOptionService.getCorrectOptionsByQuestionId(questionId);
        return ResponseEntity.ok(options);
    }

    @PostMapping("/questions/multiple")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get options by multiple question IDs",
            notes = "Get MCQ options for multiple questions",
            response = Map.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "MCQ options retrieved successfully"),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Map<Long, List<MCQOptionResponseDTO>>> getOptionsByMultipleQuestionIds(
            @ApiParam(value = "List of question IDs", required = true)
            @RequestBody List<Long> questionIds) {

        log.debug("Request to get MCQ options for {} questions", questionIds.size());
        Map<Long, List<MCQOptionResponseDTO>> optionsMap = mcqOptionService.getOptionsByMultipleQuestionIds(questionIds);
        return ResponseEntity.ok(optionsMap);
    }

    @PostMapping("/questions/multiple/active")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get active options by multiple question IDs",
            notes = "Get active MCQ options for multiple questions",
            response = Map.class
    )
    public ResponseEntity<Map<Long, List<MCQOptionResponseDTO>>> getActiveOptionsByMultipleQuestionIds(
            @ApiParam(value = "List of question IDs", required = true)
            @RequestBody List<Long> questionIds) {

        log.debug("Request to get active MCQ options for {} questions", questionIds.size());
        Map<Long, List<MCQOptionResponseDTO>> optionsMap = mcqOptionService.getActiveOptionsByMultipleQuestionIds(questionIds);
        return ResponseEntity.ok(optionsMap);
    }

    // ============ QUESTION MANAGEMENT OPERATIONS ============

    @PostMapping("/question/{questionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Create options for question",
            notes = "Create MCQ options for a specific question",
            response = MCQOptionResponseDTO.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "MCQ options created successfully"),
            @ApiResponse(code = 400, message = "Invalid input or validation failed"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "Question not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<List<MCQOptionResponseDTO>> createOptionsForQuestion(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId,
            @ApiParam(value = "List of MCQ option creation data", required = true)
            @Valid @RequestBody List<CreateMCQOptionRequestDTO> dtos) {

        log.info("Request to create {} options for question ID: {}", dtos.size(), questionId);
        List<MCQOptionResponseDTO> createdOptions = mcqOptionService.createOptionsForQuestion(questionId, dtos);
        return new ResponseEntity<>(createdOptions, HttpStatus.CREATED);
    }

    @PutMapping("/question/{questionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Update options for question",
            notes = "Update MCQ options for a specific question",
            response = MCQOptionResponseDTO.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "MCQ options updated successfully"),
            @ApiResponse(code = 400, message = "Invalid input or validation failed"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "Question not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<List<MCQOptionResponseDTO>> updateOptionsForQuestion(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId,
            @ApiParam(value = "List of MCQ option update data", required = true)
            @Valid @RequestBody List<UpdateMCQOptionRequestDTO> dtos) {

        log.info("Request to update options for question ID: {}", questionId);
        List<MCQOptionResponseDTO> updatedOptions = mcqOptionService.updateOptionsForQuestion(questionId, dtos);
        return ResponseEntity.ok(updatedOptions);
    }

    @DeleteMapping("/question/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(
            value = "Delete all options for question",
            notes = "Delete all MCQ options for a specific question"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "MCQ options deleted successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "Question not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Void> deleteAllOptionsByQuestionId(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId) {

        log.info("Request to delete all options for question ID: {}", questionId);
        mcqOptionService.deleteAllOptionsByQuestionId(questionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/question/{questionId}/replace")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Replace all options for question",
            notes = "Replace all MCQ options for a specific question with new ones",
            response = MCQOptionResponseDTO.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "MCQ options replaced successfully"),
            @ApiResponse(code = 400, message = "Invalid input or validation failed"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "Question not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Void> replaceAllOptionsForQuestion(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId,
            @ApiParam(value = "List of new MCQ option data", required = true)
            @Valid @RequestBody List<CreateMCQOptionRequestDTO> newOptions) {

        log.info("Request to replace all options for question ID: {}", questionId);
        mcqOptionService.replaceAllOptionsForQuestion(questionId, newOptions);
        return ResponseEntity.ok().build();
    }

    // ============ PAGINATION AND SEARCHING ============

    @GetMapping("/page")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get all MCQ options with pagination",
            notes = "Retrieve all MCQ options with pagination support",
            response = MCQOptionResponseDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "Page number (0-based)", dataType = "int", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "Page size", dataType = "int", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "Sort criteria", dataType = "string", paramType = "query", defaultValue = "id")
    })
    public ResponseEntity<Page<MCQOptionResponseDTO>> getAllMCQOptions(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("Request to get all MCQ options with pagination");
        Page<MCQOptionResponseDTO> options = mcqOptionService.getAllMCQOptions(pageable);
        return ResponseEntity.ok(options);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Search MCQ options",
            notes = "Search MCQ options by option text with pagination",
            response = MCQOptionResponseDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "optionText", value = "Option text to search", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "page", value = "Page number (0-based)", dataType = "int", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "Page size", dataType = "int", paramType = "query", defaultValue = "10")
    })
    public ResponseEntity<Page<MCQOptionResponseDTO>> searchMCQOptions(
            @ApiParam(value = "Option text to search", required = true)
            @RequestParam String optionText,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("Request to search MCQ options with text: {}", optionText);
        Page<MCQOptionResponseDTO> options = mcqOptionService.searchMCQOptions(optionText, pageable);
        return ResponseEntity.ok(options);
    }

    @GetMapping("/question/{questionId}/page")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get options by question with pagination",
            notes = "Get MCQ options for a question with pagination",
            response = MCQOptionResponseDTO.class
    )
    public ResponseEntity<Page<MCQOptionResponseDTO>> getOptionsByQuestionPaginated(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId,
            @PageableDefault(size = 10, sort = "optionOrder", direction = Sort.Direction.ASC) Pageable pageable) {

        log.debug("Request to get MCQ options for question ID: {} with pagination", questionId);
        Page<MCQOptionResponseDTO> options = mcqOptionService.getOptionsByQuestion(questionId, pageable);
        return ResponseEntity.ok(options);
    }

    // ============ VALIDATION AND UTILITY METHODS ============

    @GetMapping("/question/{questionId}/has-correct")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Check if question has correct option",
            notes = "Check if a question has at least one correct option"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Check completed successfully", response = Boolean.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Question not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Boolean> hasCorrectOption(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId) {

        log.debug("Request to check if question ID: {} has correct option", questionId);
        boolean hasCorrect = mcqOptionService.hasCorrectOption(questionId);
        return ResponseEntity.ok(hasCorrect);
    }

    @GetMapping("/question/{questionId}/count")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Count options by question",
            notes = "Get the count of active options for a question"
    )
    public ResponseEntity<Long> countOptionsByQuestion(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId) {

        log.debug("Request to count options for question ID: {}", questionId);
        Long count = mcqOptionService.countOptionsByQuestion(questionId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/question/{questionId}/count-correct")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Count correct options by question",
            notes = "Get the count of correct options for a question"
    )
    public ResponseEntity<Long> countCorrectOptionsByQuestion(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId) {

        log.debug("Request to count correct options for question ID: {}", questionId);
        Long count = mcqOptionService.countCorrectOptionsByQuestion(questionId);
        return ResponseEntity.ok(count);
    }

    // ============ BUSINESS LOGIC METHODS ============

    @PatchMapping("/{optionId}/mark-correct")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Mark option as correct",
            notes = "Mark a specific MCQ option as correct"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Option marked as correct successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "Option not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Void> markOptionAsCorrect(
            @ApiParam(value = "Option ID", required = true)
            @PathVariable Long optionId) {

        log.info("Request to mark option as correct: {}", optionId);
        mcqOptionService.markOptionAsCorrect(optionId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{optionId}/mark-incorrect")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Mark option as incorrect",
            notes = "Mark a specific MCQ option as incorrect"
    )
    public ResponseEntity<Void> markOptionAsIncorrect(
            @ApiParam(value = "Option ID", required = true)
            @PathVariable Long optionId) {

        log.info("Request to mark option as incorrect: {}", optionId);
        mcqOptionService.markOptionAsIncorrect(optionId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/question/{questionId}/set-correct/{optionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Set correct option for question",
            notes = "Set a specific option as correct and mark all others as incorrect"
    )
    public ResponseEntity<Void> setCorrectOption(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId,
            @ApiParam(value = "Option ID to mark as correct", required = true)
            @PathVariable Long optionId) {

        log.info("Request to set correct option {} for question {}", optionId, questionId);
        mcqOptionService.setCorrectOption(questionId, optionId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/question/{questionId}/set-multiple-correct")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Set multiple correct options",
            notes = "Set multiple options as correct for a question"
    )
    public ResponseEntity<Void> setMultipleCorrectOptions(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId,
            @ApiParam(value = "List of option IDs to mark as correct", required = true)
            @RequestBody List<Long> optionIds) {

        log.info("Request to set multiple correct options for question {}: {}", questionId, optionIds);
        mcqOptionService.setMultipleCorrectOptions(questionId, optionIds);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/question/{questionId}/reorder")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Reorder options",
            notes = "Reorder MCQ options for a question"
    )
    public ResponseEntity<Void> reorderOptions(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId,
            @ApiParam(value = "Map of option ID to new order", required = true)
            @RequestBody Map<Long, Integer> optionOrderMap) {

        log.info("Request to reorder options for question {}", questionId);
        mcqOptionService.reorderOptions(questionId, optionOrderMap);
        return ResponseEntity.ok().build();
    }

    // ============ ACTIVATION/DEACTIVATION ============

    @PatchMapping("/{optionId}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Activate option",
            notes = "Activate an MCQ option"
    )
    public ResponseEntity<Void> activateOption(
            @ApiParam(value = "Option ID", required = true)
            @PathVariable Long optionId) {

        log.info("Request to activate option: {}", optionId);
        mcqOptionService.activateOption(optionId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{optionId}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Deactivate option",
            notes = "Deactivate an MCQ option"
    )
    public ResponseEntity<Void> deactivateOption(
            @ApiParam(value = "Option ID", required = true)
            @PathVariable Long optionId) {

        log.info("Request to deactivate option: {}", optionId);
        mcqOptionService.deactivateOption(optionId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/question/{questionId}/activate-all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Activate all options for question",
            notes = "Activate all MCQ options for a specific question"
    )
    public ResponseEntity<Void> activateAllOptionsForQuestion(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId) {

        log.info("Request to activate all options for question: {}", questionId);
        mcqOptionService.activateAllOptionsForQuestion(questionId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/question/{questionId}/deactivate-all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Deactivate all options for question",notes = "Deactivate all MCQ options for a specific question"
    )
    public ResponseEntity<Void> deactivateAllOptionsForQuestion(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId) {

        log.info("Request to deactivate all options for question: {}", questionId);
        mcqOptionService.deactivateAllOptionsForQuestion(questionId);
        return ResponseEntity.ok().build();
    }

    // ============ VALIDATION ENDPOINTS ============

    @PostMapping("/question/{questionId}/validate")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Validate options for question",
            notes = "Validate MCQ options before creating them for a question"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Validation completed successfully"),
            @ApiResponse(code = 400, message = "Validation failed"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "Question not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Void> validateOptionsForQuestion(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId,
            @ApiParam(value = "List of MCQ option data to validate", required = true)
            @Valid @RequestBody List<CreateMCQOptionRequestDTO> options) {

        log.debug("Request to validate {} options for question ID: {}", options.size(), questionId);
        mcqOptionService.validateOptionsForQuestion(questionId, options);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/question/{questionId}/validate")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Validate options for question update",
            notes = "Validate MCQ options before updating them for a question"
    )
    public ResponseEntity<Void> validateOptionsForQuestionUpdate(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId,
            @ApiParam(value = "List of MCQ option update data to validate", required = true)
            @Valid @RequestBody List<UpdateMCQOptionRequestDTO> options) {

        log.debug("Request to validate options for update for question ID: {}", questionId);
        mcqOptionService.validateOptionsForQuestionUpdate(questionId, options);
        return ResponseEntity.ok().build();
    }

    // ============ UTILITY ENDPOINTS ============

    @GetMapping("/question/{questionId}/summary")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get option summary for question",
            notes = "Get a summary of MCQ options for a question including counts and statistics"
    )
    public ResponseEntity<Map<String, Object>> getOptionSummaryForQuestion(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long questionId) {

        log.debug("Request to get option summary for question ID: {}", questionId);

        Map<String, Object> summary = Map.of(
                "totalOptions", mcqOptionService.countOptionsByQuestion(questionId),
                "correctOptions", mcqOptionService.countCorrectOptionsByQuestion(questionId),
                "hasCorrectOption", mcqOptionService.hasCorrectOption(questionId)
        );

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/stats/global")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(
            value = "Get global MCQ option statistics",
            notes = "Get global statistics for all MCQ options (Admin only)"
    )
    public ResponseEntity<Map<String, Object>> getGlobalOptionStats() {
        log.debug("Request to get global MCQ option statistics");

        // This would need to be implemented in the service layer
        Map<String, Object> stats = Map.of(
                "message", "Global statistics endpoint - implementation pending"
        );

        return ResponseEntity.ok(stats);
    }

    // ============ LEGACY SUPPORT (for backward compatibility) ============

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(value = "Legacy get all options endpoint", hidden = true)
    public ResponseEntity<Page<MCQOptionResponseDTO>> getAllMCQOptionsLegacy(Pageable pageable) {
        return getAllMCQOptions(pageable);
    }

    @PostMapping("/multiple")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(value = "Legacy bulk create endpoint", hidden = true)
    public ResponseEntity<List<MCQOptionResponseDTO>> createMCQOptionsLegacy(
            @Valid @RequestBody List<CreateMCQOptionRequestDTO> dtos) {
        return createMCQOptions(dtos);
    }

    @PostMapping("/options-by-ids")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(value = "Legacy get options by IDs endpoint", hidden = true)
    public ResponseEntity<Map<Long, List<MCQOptionResponseDTO>>> getOptionsByMultipleQuestionIdsLegacy(
            @RequestBody List<Long> questionIds) {
        return getOptionsByMultipleQuestionIds(questionIds);
    }

    @GetMapping("/{questionId}/options")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(value = "Legacy get options by question endpoint", hidden = true)
    public ResponseEntity<List<MCQOptionResponseDTO>> getOptionsByQuestionIdLegacy(
            @PathVariable Long questionId) {
        return getOptionsByQuestionId(questionId);
    }
}