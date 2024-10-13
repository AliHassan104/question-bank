package com.example.questionbank.service;

import com.example.questionbank.model.ClassEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClassService {
    ClassEntity createClassEntity(ClassEntity classEntity);
    ClassEntity updateClassEntity(Long id, ClassEntity classEntity);
    void deleteClassEntity(Long id);
    ClassEntity getClassEntityById(Long id);

    // Pagination and searching
    List<ClassEntity> getAllClassEntities();
    Page<ClassEntity> searchClassEntities(String name, Pageable pageable);

}
