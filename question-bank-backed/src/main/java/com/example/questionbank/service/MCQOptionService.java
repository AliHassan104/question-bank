package com.example.questionbank.service;

import com.example.questionbank.model.MCQOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface MCQOptionService {
    MCQOption createMCQOption(MCQOption mcqOption);
    MCQOption updateMCQOption(Long id, MCQOption mcqOption);
    void deleteMCQOption(Long id);
    MCQOption getMCQOptionById(Long id);

    // Pagination and searching
    Page<MCQOption> getAllMCQOptions(Pageable pageable);
    Page<MCQOption> searchMCQOptions(String optionText, Pageable pageable);

    List<MCQOption> createMCQOptions(List<MCQOption> mcqOptions);

    List<MCQOption> getOptionsByQuestionId(Long questionId);


    Map<Long, List<MCQOption>> getOptionsByMultipleQuestionIds(List<Long> questionIds);
}

