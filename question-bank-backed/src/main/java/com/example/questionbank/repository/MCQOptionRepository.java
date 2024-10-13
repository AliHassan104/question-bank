package com.example.questionbank.repository;

import com.example.questionbank.model.MCQOption;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MCQOptionRepository extends JpaRepository<MCQOption, Long> {
    Page<MCQOption> findByOptionTextContainingIgnoreCase(String optionText, Pageable pageable);

    List<MCQOption> findByQuestionId(Long questionId);

    List<MCQOption> findByQuestionIdIn(List<Long> questionIds);
}

