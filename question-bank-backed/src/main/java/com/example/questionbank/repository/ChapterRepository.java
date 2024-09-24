package com.example.questionbank.repository;

import com.example.questionbank.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    // Custom query to search chapters by name (case insensitive)
    Page<Chapter> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

