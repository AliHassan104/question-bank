package com.example.questionbank.service;

import com.example.questionbank.model.MCQOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MCQOptionService {
    MCQOption createMCQOption(MCQOption mcqOption);
    MCQOption updateMCQOption(Long id, MCQOption mcqOption);
    void deleteMCQOption(Long id);
    MCQOption getMCQOptionById(Long id);

    // Pagination and searching
    Page<MCQOption> getAllMCQOptions(Pageable pageable);
    Page<MCQOption> searchMCQOptions(String optionText, Pageable pageable);
}

