package com.example.questionbank.service.impl;

import com.example.questionbank.model.MCQOption;
import com.example.questionbank.repository.MCQOptionRepository;
import com.example.questionbank.service.MCQOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MCQOptionServiceImplementation implements MCQOptionService {

    @Autowired
    private MCQOptionRepository mcqOptionRepository;

    @Override
    public MCQOption createMCQOption(MCQOption mcqOption) {
        return mcqOptionRepository.save(mcqOption);
    }

    @Override
    public MCQOption updateMCQOption(Long id, MCQOption updatedMCQOption) {
        Optional<MCQOption> existingMCQOptionOpt = mcqOptionRepository.findById(id);
        if (existingMCQOptionOpt.isPresent()) {
            MCQOption existingMCQOption = existingMCQOptionOpt.get();
            existingMCQOption.setOptionText(updatedMCQOption.getOptionText());
            existingMCQOption.setCorrect(updatedMCQOption.isCorrect());
            existingMCQOption.setQuestion(updatedMCQOption.getQuestion());
            return mcqOptionRepository.save(existingMCQOption);
        } else {
            throw new RuntimeException("MCQOption not found with id " + id);
        }
    }

    @Override
    public void deleteMCQOption(Long id) {
        mcqOptionRepository.deleteById(id);
    }

    @Override
    public MCQOption getMCQOptionById(Long id) {
        return mcqOptionRepository.findById(id).orElseThrow(() ->
                new RuntimeException("MCQOption not found with id " + id));
    }

    @Override
    public Page<MCQOption> getAllMCQOptions(Pageable pageable) {
        return mcqOptionRepository.findAll(pageable);
    }

    @Override
    public Page<MCQOption> searchMCQOptions(String optionText, Pageable pageable) {
        return mcqOptionRepository.findByOptionTextContainingIgnoreCase(optionText, pageable);
    }
}
