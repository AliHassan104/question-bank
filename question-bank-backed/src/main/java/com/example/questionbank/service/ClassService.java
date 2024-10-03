package com.example.questionbank.service;

import com.example.questionbank.model.ClassEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClassService {
    ClassEntity createClassEntity(ClassEntity classEntity);
    ClassEntity updateClassEntity(Long id, ClassEntity classEntity);
    void deleteClassEntity(Long id);
    ClassEntity getClassEntityById(Long id);

    // Pagination and searching
    Page<ClassEntity> getAllClassEntities(Pageable pageable);
    Page<ClassEntity> searchClassEntities(String name, Pageable pageable);

}
