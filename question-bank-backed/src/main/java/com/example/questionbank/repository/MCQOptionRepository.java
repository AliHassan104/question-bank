package com.example.questionbank.repository;

import com.example.questionbank.model.MCQOption;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface MCQOptionRepository extends JpaRepository<MCQOption, Long> {
    Page<MCQOption> findByOptionTextContainingIgnoreCase(String optionText, Pageable pageable);
}

