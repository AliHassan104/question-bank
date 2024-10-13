package com.example.questionbank.controller;

import com.example.questionbank.model.Subject;
import com.example.questionbank.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @PostMapping
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject) {
        Subject createdSubject = subjectService.createSubject(subject);
        return new ResponseEntity<>(createdSubject, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subject> updateSubject(@PathVariable Long id, @RequestBody Subject subject) {
        Subject updatedSubject = subjectService.updateSubject(id, subject);
        return ResponseEntity.ok(updatedSubject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        Subject subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(subject);
    }

    @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects() {
        List<Subject> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Subject>> getAllSubjectsWithPagination(Pageable pageable) {
        Page<Subject> subjects = subjectService.getAllSubjectsWithPagination(pageable);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Subject>> searchSubjects(@RequestParam String name) {
        List<Subject> subjects = subjectService.searchSubjects(name);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/filter-by-class")
    public ResponseEntity<List<Subject>> filterSubjectsByClass(
            @RequestParam Long classId) {
        List<Subject> subjects = subjectService.filterSubjectsByClass(classId);
        return ResponseEntity.ok(subjects);
    }
}

