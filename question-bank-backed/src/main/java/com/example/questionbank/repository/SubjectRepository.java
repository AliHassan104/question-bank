package com.example.questionbank.repository;

import com.example.questionbank.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Page<Subject> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

