package com.example.questionbank.controller;

import com.example.questionbank.model.Question;
import com.example.questionbank.model.enums.SectionType;
import com.example.questionbank.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) {
        Question createdQuestion = questionService.createQuestion(question);
        return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        Question updatedQuestion = questionService.updateQuestion(id, question);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    @GetMapping
    public ResponseEntity<Page<Question>> getAllQuestions(Pageable pageable) {
        Page<Question> questions = questionService.getAllQuestions(pageable);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Question>> searchQuestions(@RequestParam String questionText, Pageable pageable) {
        Page<Question> questions = questionService.searchQuestions(questionText, pageable);
        return ResponseEntity.ok(questions);
    }

    // GET /api/questions/filter?sectionType=MCQ&classId=1&subjectId=2&page=0&size=10
    @GetMapping("/filter")
    public ResponseEntity<Page<Question>> getFilteredQuestions(
            @RequestParam(required = false) SectionType sectionType,
            @RequestParam(required = false) Long chapterId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long classId,
            Pageable pageable) {

        Page<Question> filteredQuestions = questionService.getFilteredQuestions(sectionType, chapterId, subjectId, classId, pageable);
        return ResponseEntity.ok(filteredQuestions);
    }
}

