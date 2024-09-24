package com.example.questionbank.service;

import com.example.questionbank.model.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubjectService {
    Subject createSubject(Subject subject);
    Subject updateSubject(Long id, Subject subject);
    void deleteSubject(Long id);
    Subject getSubjectById(Long id);

    // Pagination and searching
    Page<Subject> getAllSubjects(Pageable pageable);
    Page<Subject> searchSubjects(String name, Pageable pageable);
}
