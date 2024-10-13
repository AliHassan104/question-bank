package com.example.questionbank.service;

import com.example.questionbank.model.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubjectService {

    Subject createSubject(Subject subject);

    Subject updateSubject(Long id, Subject subject);

    void deleteSubject(Long id);

    Subject getSubjectById(Long id);

    List<Subject> getAllSubjects();

    List<Subject> searchSubjects(String name);

    List<Subject> filterSubjectsByClass(Long classId);

    Page<Subject> getAllSubjectsWithPagination(Pageable pageable);
}
