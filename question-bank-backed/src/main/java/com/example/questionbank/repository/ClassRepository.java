package com.example.questionbank.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.questionbank.model.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    // Custom query to search by name (case insensitive)
    Page<ClassEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
