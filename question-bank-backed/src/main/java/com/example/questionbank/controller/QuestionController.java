package com.example.questionbank.controller;

import com.example.questionbank.dto.request.CreateQuestionRequestDTO;
import com.example.questionbank.dto.request.UpdateQuestionRequestDTO;
import com.example.questionbank.dto.response.QuestionResponseDTO;
import com.example.questionbank.model.enums.SectionType;
import com.example.questionbank.model.enums.QuestionType;
import com.example.questionbank.model.enums.DifficultyLevel;
import com.example.questionbank.service.QuestionService;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/questions")
@Api(tags = "Questions", description = "Question management operations")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // ============ BASIC CRUD OPERATIONS ============

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Create new question",
            notes = "Create a new question with optional MCQ options (Admin/Teacher only)",
            response = QuestionResponseDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Question created successfully", response = QuestionResponseDTO.class),
            @ApiResponse(code = 400, message = "Invalid input or validation failed"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied - Admin/Teacher role required"),
            @ApiResponse(code = 404, message = "Chapter not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<QuestionResponseDTO> createQuestion(
            @ApiParam(value = "Question creation data", required = true)
            @Valid @RequestBody CreateQuestionRequestDTO dto) {

        log.info("Request to create question");
        QuestionResponseDTO createdQuestion = questionService.createQuestion(dto);
        return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Update question",
            notes = "Update an existing question (Admin/Teacher only)",
            response = QuestionResponseDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Question updated successfully", response = QuestionResponseDTO.class),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "Question or Chapter not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<QuestionResponseDTO> updateQuestion(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "Question update data", required = true)
            @Valid @RequestBody UpdateQuestionRequestDTO dto) {

        log.info("Request to update question with ID: {}", id);
        QuestionResponseDTO updatedQuestion = questionService.updateQuestion(id, dto);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(
            value = "Delete question",
            notes = "Soft delete a question (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Question deleted successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Access denied - Admin role required"),
            @ApiResponse(code = 404, message = "Question not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Void> deleteQuestion(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long id) {

        log.info("Request to delete question with ID: {}", id);
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get question by ID",
            notes = "Retrieve a specific question by its ID",
            response = QuestionResponseDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Question retrieved successfully", response = QuestionResponseDTO.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Question not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<QuestionResponseDTO> getQuestionById(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long id) {

        log.debug("Request to get question with ID: {}", id);
        QuestionResponseDTO question = questionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    @GetMapping("/{id}/with-options")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get question with MCQ options",
            notes = "Retrieve a specific question with its MCQ options",
            response = QuestionResponseDTO.class
    )
    public ResponseEntity<QuestionResponseDTO> getQuestionByIdWithOptions(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long id) {

        log.debug("Request to get question with options, ID: {}", id);
        QuestionResponseDTO question = questionService.getQuestionByIdWithOptions(id);
        return ResponseEntity.ok(question);
    }

    // ============ LISTING AND PAGINATION ============

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get all questions",
            notes = "Retrieve all questions without pagination",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> getAllQuestions() {
        log.debug("Request to get all questions");
        List<QuestionResponseDTO> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get all questions with pagination",
            notes = "Retrieve all questions with pagination support",
            response = QuestionResponseDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "Page number (0-based)", dataType = "int", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "Page size", dataType = "int", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "Sort criteria", dataType = "string", paramType = "query", defaultValue = "id")
    })
    public ResponseEntity<Page<QuestionResponseDTO>> getAllQuestionsPagination(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("Request to get all questions with pagination");
        Page<QuestionResponseDTO> questions = questionService.getAllQuestionsPagination(pageable);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get all active questions",
            notes = "Retrieve all active questions",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> getAllActiveQuestions() {
        log.debug("Request to get all active questions");
        List<QuestionResponseDTO> questions = questionService.getAllActiveQuestions();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/active/page")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get active questions with pagination",
            notes = "Retrieve all active questions with pagination support",
            response = QuestionResponseDTO.class
    )
    public ResponseEntity<Page<QuestionResponseDTO>> getAllActiveQuestionsPagination(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("Request to get active questions with pagination");
        Page<QuestionResponseDTO> questions = questionService.getAllActiveQuestionsPagination(pageable);
        return ResponseEntity.ok(questions);
    }

    // ============ SEARCH OPERATIONS ============

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Search questions",
            notes = "Search questions by text with pagination",
            response = QuestionResponseDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionText", value = "Question text to search", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "page", value = "Page number (0-based)", dataType = "int", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "size", value = "Page size", dataType = "int", paramType = "query", defaultValue = "10")
    })
    public ResponseEntity<Page<QuestionResponseDTO>> searchQuestions(
            @ApiParam(value = "Question text to search", required = true)
            @RequestParam String questionText,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("Request to search questions with text: {}", questionText);
        Page<QuestionResponseDTO> questions = questionService.searchQuestions(questionText, pageable);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/search/active")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Search active questions",
            notes = "Search only active questions by text",
            response = QuestionResponseDTO.class
    )
    public ResponseEntity<Page<QuestionResponseDTO>> searchActiveQuestions(
            @ApiParam(value = "Search text", required = true)
            @RequestParam String searchText,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("Request to search active questions with text: {}", searchText);
        Page<QuestionResponseDTO> questions = questionService.searchActiveQuestions(searchText, pageable);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/search/keywords")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Search by keywords",
            notes = "Search questions using multiple keywords",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> searchQuestionsByKeywords(
            @ApiParam(value = "List of keywords", required = true)
            @RequestBody List<String> keywords) {

        log.debug("Request to search questions by keywords: {}", keywords);
        List<QuestionResponseDTO> questions = questionService.searchQuestionsByKeywords(keywords);
        return ResponseEntity.ok(questions);
    }

    // ============ FILTERING OPERATIONS ============

    @GetMapping("/filter")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Filter questions",
            notes = "Filter questions by multiple criteria with pagination",
            response = QuestionResponseDTO.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sectionType", value = "Section type", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "questionType", value = "Question type", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "difficultyLevel", value = "Difficulty level", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "chapterId", value = "Chapter ID", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "subjectId", value = "Subject ID", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "classId", value = "Class ID", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "isAddedToPaper", value = "Added to paper status", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "minMarks", value = "Minimum marks", dataType = "double", paramType = "query"),
            @ApiImplicitParam(name = "maxMarks", value = "Maximum marks", dataType = "double", paramType = "query")
    })
    public ResponseEntity<Page<QuestionResponseDTO>> getFilteredQuestions(
            @RequestParam(required = false) SectionType sectionType,
            @RequestParam(required = false) QuestionType questionType,
            @RequestParam(required = false) DifficultyLevel difficultyLevel,
            @RequestParam(required = false) Long chapterId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Boolean isAddedToPaper,
            @RequestParam(required = false) Double minMarks,
            @RequestParam(required = false) Double maxMarks,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("Request to filter questions with multiple criteria");
        Page<QuestionResponseDTO> questions = questionService.getFilteredQuestions(
                sectionType, questionType, difficultyLevel, chapterId, subjectId,
                classId, isAddedToPaper, minMarks, maxMarks, pageable);
        return ResponseEntity.ok(questions);
    }

    // ============ CHAPTER-BASED OPERATIONS ============

    @GetMapping("/chapter/{chapterId}")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get questions by chapter",
            notes = "Get all active questions for a specific chapter",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByChapter(
            @ApiParam(value = "Chapter ID", required = true)
            @PathVariable Long chapterId) {

        log.debug("Request to get questions for chapter ID: {}", chapterId);
        List<QuestionResponseDTO> questions = questionService.getQuestionsByChapter(chapterId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/chapter/{chapterId}/page")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get questions by chapter with pagination",
            notes = "Get questions for a chapter with pagination",
            response = QuestionResponseDTO.class
    )
    public ResponseEntity<Page<QuestionResponseDTO>> getQuestionsByChapterPaginated(
            @ApiParam(value = "Chapter ID", required = true)
            @PathVariable Long chapterId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("Request to get questions for chapter ID: {} with pagination", chapterId);
        Page<QuestionResponseDTO> questions = questionService.getQuestionsByChapter(chapterId, pageable);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/chapter/{chapterId}/count")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Count questions by chapter",
            notes = "Get the count of questions in a chapter"
    )
    public ResponseEntity<Long> countQuestionsByChapter(
            @ApiParam(value = "Chapter ID", required = true)
            @PathVariable Long chapterId) {

        log.debug("Request to count questions for chapter ID: {}", chapterId);
        Long count = questionService.countQuestionsByChapter(chapterId);
        return ResponseEntity.ok(count);
    }

    // ============ SUBJECT-BASED OPERATIONS ============

    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get questions by subject",
            notes = "Get all questions for a specific subject",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsBySubject(
            @ApiParam(value = "Subject ID", required = true)
            @PathVariable Long subjectId) {

        log.debug("Request to get questions for subject ID: {}", subjectId);
        List<QuestionResponseDTO> questions = questionService.getQuestionsBySubject(subjectId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/subject/{subjectId}/added-to-paper")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get questions added to paper by subject",
            notes = "Get questions that are added to paper for a subject",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsBySubjectAndAddedToPaper(
            @ApiParam(value = "Subject ID", required = true)
            @PathVariable Long subjectId) {

        log.debug("Request to get questions added to paper for subject ID: {}", subjectId);
        List<QuestionResponseDTO> questions = questionService.getQuestionsBySubjectAndAddedToPaper(subjectId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/subject/{subjectId}/not-added-to-paper")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get questions not added to paper by subject",
            notes = "Get questions that are not added to paper for a subject",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsBySubjectAndNotAddedToPaper(
            @ApiParam(value = "Subject ID", required = true)
            @PathVariable Long subjectId) {

        log.debug("Request to get questions not added to paper for subject ID: {}", subjectId);
        List<QuestionResponseDTO> questions = questionService.getQuestionsBySubjectAndNotAddedToPaper(subjectId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/subject/{subjectId}/grouped-by-section")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get questions grouped by section type",
            notes = "Get questions for a subject grouped by section type",
            response = Map.class
    )
    public ResponseEntity<Map<SectionType, List<QuestionResponseDTO>>> getQuestionsBySubjectGroupedBySection(
            @ApiParam(value = "Subject ID", required = true)
            @PathVariable Long subjectId) {

        log.debug("Request to get questions grouped by section for subject ID: {}", subjectId);
        Map<SectionType, List<QuestionResponseDTO>> questions = questionService.getQuestionsBySubjectGroupedBySection(subjectId);
        return ResponseEntity.ok(questions);
    }

    // ============ SECTION TYPE OPERATIONS ============

    @GetMapping("/section/{sectionType}")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get questions by section type",
            notes = "Get questions filtered by section type",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsBySectionType(
            @ApiParam(value = "Section type", required = true)
            @PathVariable SectionType sectionType) {

        log.debug("Request to get questions by section type: {}", sectionType);
        List<QuestionResponseDTO> questions = questionService.getQuestionsBySectionType(sectionType);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/mcq")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get MCQ questions",
            notes = "Get all MCQ questions",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> getMCQQuestions() {
        log.debug("Request to get MCQ questions");
        List<QuestionResponseDTO> questions = questionService.getMCQQuestions();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/short-answer")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get short answer questions",
            notes = "Get all short answer questions",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> getShortAnswerQuestions() {
        log.debug("Request to get short answer questions");
        List<QuestionResponseDTO> questions = questionService.getShortAnswerQuestions();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/long-answer")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get long answer questions",
            notes = "Get all long answer questions",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> getLongAnswerQuestions() {
        log.debug("Request to get long answer questions");
        List<QuestionResponseDTO> questions = questionService.getLongAnswerQuestions();
        return ResponseEntity.ok(questions);
    }

    // ============ PAPER MANAGEMENT ============

    @PatchMapping("/{id}/toggle-paper-status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Toggle paper status",
            notes = "Toggle the added-to-paper status of a question",
            response = QuestionResponseDTO.class
    )
    public ResponseEntity<QuestionResponseDTO> toggleAddedToPaper(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long id) {

        log.info("Request to toggle paper status for question ID: {}", id);
        QuestionResponseDTO updatedQuestion = questionService.toggleAddedToPaper(id);
        return ResponseEntity.ok(updatedQuestion);
    }

    @PatchMapping("/{id}/add-to-paper")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Add question to paper",
            notes = "Add a question to paper"
    )
    public ResponseEntity<Void> addQuestionToPaper(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long id) {

        log.info("Request to add question to paper: {}", id);
        questionService.addQuestionToPaper(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/remove-from-paper")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Remove question from paper",
            notes = "Remove a question from paper"
    )
    public ResponseEntity<Void> removeQuestionFromPaper(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long id) {

        log.info("Request to remove question from paper: {}", id);
        questionService.removeQuestionFromPaper(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/added-to-paper")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get questions added to paper",
            notes = "Get all questions that are added to paper",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsAddedToPaper() {
        log.debug("Request to get questions added to paper");
        List<QuestionResponseDTO> questions = questionService.getQuestionsAddedToPaper();
        return ResponseEntity.ok(questions);
    }

    // ============ STATISTICS AND REPORTING ============

    @GetMapping("/subject/{subjectId}/stats")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get question statistics by subject",
            notes = "Get comprehensive question statistics for a subject"
    )
    public ResponseEntity<Map<String, Long>> getQuestionStatsBySubject(
            @ApiParam(value = "Subject ID", required = true)
            @PathVariable Long subjectId) {

        log.debug("Request to get question stats for subject ID: {}", subjectId);
        Map<String, Long> stats = questionService.getQuestionStatsBySubject(subjectId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/chapter/{chapterId}/stats")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get question statistics by chapter",
            notes = "Get question statistics for a chapter"
    )
    public ResponseEntity<Map<String, Long>> getQuestionStatsByChapter(
            @ApiParam(value = "Chapter ID", required = true)
            @PathVariable Long chapterId) {

        log.debug("Request to get question stats for chapter ID: {}", chapterId);
        Map<String, Long> stats = questionService.getQuestionStatsByChapter(chapterId);
        return ResponseEntity.ok(stats);
    }

    // ============ RANDOM QUESTION SELECTION ============

    @GetMapping("/random")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get random questions",
            notes = "Get random questions",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> getRandomQuestions(
            @ApiParam(value = "Number of questions", required = true)
            @RequestParam int count) {

        log.debug("Request to get {} random questions", count);
        List<QuestionResponseDTO> questions = questionService.getRandomQuestions(count);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/subject/{subjectId}/random")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get random questions by subject",
            notes = "Get random questions from a specific subject",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> getRandomQuestionsBySubject(
            @ApiParam(value = "Subject ID", required = true)
            @PathVariable Long subjectId,
            @ApiParam(value = "Number of questions", required = true)
            @RequestParam int count) {

        log.debug("Request to get {} random questions for subject ID: {}", count, subjectId);
        List<QuestionResponseDTO> questions = questionService.getRandomQuestionsBySubject(subjectId, count);
        return ResponseEntity.ok(questions);
    }

    // ============ QUESTION DUPLICATION ============

    @PostMapping("/{id}/duplicate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Duplicate question",
            notes = "Create a copy of an existing question",
            response = QuestionResponseDTO.class
    )
    public ResponseEntity<QuestionResponseDTO> duplicateQuestion(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long id) {

        log.info("Request to duplicate question ID: {}", id);
        QuestionResponseDTO duplicatedQuestion = questionService.duplicateQuestion(id);
        return ResponseEntity.ok(duplicatedQuestion);
    }

    @PostMapping("/{id}/duplicate-to-chapter/{chapterId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Duplicate question to chapter",
            notes = "Duplicate a question to a different chapter",
            response = QuestionResponseDTO.class
    )
    public ResponseEntity<QuestionResponseDTO> duplicateQuestionToChapter(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "Target Chapter ID", required = true)
            @PathVariable Long chapterId) {

        log.info("Request to duplicate question ID: {} to chapter ID: {}", id, chapterId);
        QuestionResponseDTO duplicatedQuestion = questionService.duplicateQuestionToChapter(id, chapterId);
        return ResponseEntity.ok(duplicatedQuestion);
    }

    // ============ VALIDATION ============

    @GetMapping("/{id}/validate")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Validate question for paper",
            notes = "Check if a question is valid for adding to paper"
    )
    public ResponseEntity<Boolean> validateQuestionForPaper(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long id) {

        log.debug("Request to validate question ID: {}", id);
        boolean isValid = questionService.validateQuestionForPaper(id);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/{id}/validation-errors")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get validation errors for question",
            notes = "Get list of validation errors for a question"
    )
    public ResponseEntity<List<String>> getValidationErrorsForQuestion(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long id) {

        log.debug("Request to get validation errors for question ID: {}", id);
        List<String> errors = questionService.getValidationErrorsForQuestion(id);
        return ResponseEntity.ok(errors);
    }

    // ============ ACTIVATION/DEACTIVATION ============

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Activate question",
            notes = "Activate a question"
    )
    public ResponseEntity<Void> activateQuestion(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long id) {

        log.info("Request to activate question ID: {}", id);
        questionService.activateQuestion(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Deactivate question",
            notes = "Deactivate a question"
    )
    public ResponseEntity<Void> deactivateQuestion(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long id) {

        log.info("Request to deactivate question ID: {}", id);
        questionService.deactivateQuestion(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Activate multiple questions",
            notes = "Activate multiple questions by IDs"
    )
    public ResponseEntity<Void> activateQuestions(
            @ApiParam(value = "List of question IDs", required = true)
            @RequestBody List<Long> ids) {

        log.info("Request to activate {} questions", ids.size());
        questionService.activateQuestions(ids);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Deactivate multiple questions",
            notes = "Deactivate multiple questions by IDs"
    )
    public ResponseEntity<Void> deactivateQuestions(
            @ApiParam(value = "List of question IDs", required = true)
            @RequestBody List<Long> ids) {

        log.info("Request to deactivate {} questions", ids.size());
        questionService.deactivateQuestions(ids);
        return ResponseEntity.ok().build();
    }

    // ============ BULK OPERATIONS ============

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Create multiple questions",
            notes = "Create multiple questions in bulk",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> createQuestions(
            @ApiParam(value = "List of question creation data", required = true)
            @Valid @RequestBody List<CreateQuestionRequestDTO> dtos) {

        log.info("Request to create {} questions in bulk", dtos.size());
        List<QuestionResponseDTO> createdQuestions = questionService.createQuestions(dtos);
        return new ResponseEntity<>(createdQuestions, HttpStatus.CREATED);
    }

    @PutMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Update multiple questions",
            notes = "Update multiple questions in bulk",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> updateQuestions(
            @ApiParam(value = "List of question update data", required = true)
            @Valid @RequestBody List<UpdateQuestionRequestDTO> dtos) {

        log.info("Request to update {} questions in bulk", dtos.size());
        List<QuestionResponseDTO> updatedQuestions = questionService.updateQuestions(dtos);
        return ResponseEntity.ok(updatedQuestions);
    }

    @DeleteMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(
            value = "Delete multiple questions",
            notes = "Delete multiple questions by IDs"
    )
    public ResponseEntity<Void> deleteQuestions(
            @ApiParam(value = "List of question IDs", required = true)
            @RequestBody List<Long> ids) {

        log.info("Request to delete {} questions in bulk", ids.size());
        questionService.deleteQuestionsByIds(ids);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk/add-to-paper")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Add multiple questions to paper",
            notes = "Add multiple questions to paper by IDs"
    )
    public ResponseEntity<Void> addQuestionsToPaper(
            @ApiParam(value = "List of question IDs", required = true)
            @RequestBody List<Long> questionIds) {

        log.info("Request to add {} questions to paper", questionIds.size());
        questionService.addQuestionsToPaper(questionIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk/remove-from-paper")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @ApiOperation(
            value = "Remove multiple questions from paper",
            notes = "Remove multiple questions from paper by IDs"
    )
    public ResponseEntity<Void> removeQuestionsFromPaper(
            @ApiParam(value = "List of question IDs", required = true)
            @RequestBody List<Long> questionIds) {

        log.info("Request to remove {} questions from paper", questionIds.size());
        questionService.removeQuestionsFromPaper(questionIds);
        return ResponseEntity.ok().build();
    }

    // ============ MARKS AND TOTALS ============

    @GetMapping("/subject/{subjectId}/total-marks")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get total marks by subject",
            notes = "Get total marks for all questions added to paper in a subject"
    )
    public ResponseEntity<Double> getTotalMarksBySubject(
            @ApiParam(value = "Subject ID", required = true)
            @PathVariable Long subjectId) {

        log.debug("Request to get total marks for subject ID: {}", subjectId);
        Double totalMarks = questionService.getTotalMarksBySubject(subjectId);
        return ResponseEntity.ok(totalMarks);
    }

    @GetMapping("/chapter/{chapterId}/total-marks")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get total marks by chapter",
            notes = "Get total marks for all questions added to paper in a chapter"
    )
    public ResponseEntity<Double> getTotalMarksByChapter(
            @ApiParam(value = "Chapter ID", required = true)
            @PathVariable Long chapterId) {

        log.debug("Request to get total marks for chapter ID: {}", chapterId);
        Double totalMarks = questionService.getTotalMarksByChapter(chapterId);
        return ResponseEntity.ok(totalMarks);
    }

    @GetMapping("/subject/{subjectId}/marks-by-section")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get total marks by section",
            notes = "Get total marks grouped by section type for a subject"
    )
    public ResponseEntity<Map<SectionType, Double>> getTotalMarksBySubjectAndSection(
            @ApiParam(value = "Subject ID", required = true)
            @PathVariable Long subjectId) {

        log.debug("Request to get marks by section for subject ID: {}", subjectId);
        Map<SectionType, Double> marksBySection = questionService.getTotalMarksBySubjectAndSection(subjectId);
        return ResponseEntity.ok(marksBySection);
    }

    // ============ ADVANCED SEARCH ============

    @PostMapping("/advanced-search")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Advanced search",
            notes = "Perform advanced search with multiple criteria",
            response = QuestionResponseDTO.class
    )
    public ResponseEntity<Page<QuestionResponseDTO>> advancedSearch(
            @ApiParam(value = "Search text") @RequestParam(required = false) String searchText,
            @ApiParam(value = "Section types") @RequestParam(required = false) List<SectionType> sectionTypes,
            @ApiParam(value = "Question types") @RequestParam(required = false) List<QuestionType> questionTypes,
            @ApiParam(value = "Difficulty levels") @RequestParam(required = false) List<DifficultyLevel> difficultyLevels,
            @ApiParam(value = "Chapter IDs") @RequestParam(required = false) List<Long> chapterIds,
            @ApiParam(value = "Subject IDs") @RequestParam(required = false) List<Long> subjectIds,
            @ApiParam(value = "Class IDs") @RequestParam(required = false) List<Long> classIds,
            @ApiParam(value = "Added to paper status") @RequestParam(required = false) Boolean isAddedToPaper,
            @ApiParam(value = "Active status") @RequestParam(required = false) Boolean isActive,
            @ApiParam(value = "Minimum marks") @RequestParam(required = false) Double minMarks,
            @ApiParam(value = "Maximum marks") @RequestParam(required = false) Double maxMarks,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("Request for advanced search with multiple criteria");
        Page<QuestionResponseDTO> questions = questionService.advancedSearch(
                searchText, sectionTypes, questionTypes, difficultyLevels,
                chapterIds, subjectIds, classIds, isAddedToPaper, isActive,
                minMarks, maxMarks, pageable);
        return ResponseEntity.ok(questions);
    }

    // ============ RECOMMENDATIONS ============

    @GetMapping("/{id}/recommendations")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get recommended questions",
            notes = "Get questions recommended based on a specific question",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> getRecommendedQuestions(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "Number of recommendations", required = true)
            @RequestParam int count) {

        log.debug("Request to get {} recommended questions for question ID: {}", count, id);
        List<QuestionResponseDTO> questions = questionService.getRecommendedQuestions(id, count);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}/similar")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get similar questions",
            notes = "Get questions similar to a specific question",
            response = QuestionResponseDTO.class,
            responseContainer = "List"
    )
    public ResponseEntity<List<QuestionResponseDTO>> getSimilarQuestions(
            @ApiParam(value = "Question ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "Number of similar questions", required = true)
            @RequestParam int count) {

        log.debug("Request to get {} similar questions for question ID: {}", count, id);
        List<QuestionResponseDTO> questions = questionService.getSimilarQuestions(id, count);
        return ResponseEntity.ok(questions);
    }

    // ============ COUNTS AND STATISTICS ============

    @GetMapping("/subject/{subjectId}/count-by-section")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get question count by section type",
            notes = "Get question counts grouped by section type for a subject"
    )
    public ResponseEntity<Map<SectionType, Long>> getQuestionCountBySectionType(
            @ApiParam(value = "Subject ID", required = true)
            @PathVariable Long subjectId) {

        log.debug("Request to get question count by section for subject ID: {}", subjectId);
        Map<SectionType, Long> counts = questionService.getQuestionCountBySectionType(subjectId);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/subject/{subjectId}/count-by-difficulty")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(
            value = "Get question count by difficulty",
            notes = "Get question counts grouped by difficulty level for a subject"
    )
    public ResponseEntity<Map<DifficultyLevel, Long>> getQuestionCountByDifficulty(
            @ApiParam(value = "Subject ID", required = true)
            @PathVariable Long subjectId) {

        log.debug("Request to get question count by difficulty for subject ID: {}", subjectId);
        Map<DifficultyLevel, Long> counts = questionService.getQuestionCountByDifficulty(subjectId);
        return ResponseEntity.ok(counts);
    }

    // ============ LEGACY ENDPOINTS (for backward compatibility) ============

    @GetMapping("/pagination")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    @ApiOperation(value = "Legacy pagination endpoint", hidden = true)
    public ResponseEntity<Page<QuestionResponseDTO>> getAllQuestionsPaginationLegacy(Pageable pageable) {
        return getAllQuestionsPagination(pageable);
    }
}