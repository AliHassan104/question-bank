package com.example.questionbank.service.impl;

import com.example.questionbank.model.MCQOption;
import com.example.questionbank.repository.MCQOptionRepository;
import com.example.questionbank.service.MCQOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<MCQOption> createMCQOptions(List<MCQOption> mcqOptions) {
        return mcqOptionRepository.saveAll(mcqOptions);
    }

    public List<MCQOption> getOptionsByQuestionId(Long questionId) {
        return mcqOptionRepository.findByQuestionId(questionId);
    }

    public Map<Long, List<MCQOption>> getOptionsByMultipleQuestionIds(List<Long> questionIds) {
        List<MCQOption> options = mcqOptionRepository.findByQuestionIdIn(questionIds);

        // Group the options by questionId
        return options.stream().collect(Collectors.groupingBy(option -> option.getQuestion().getId()));
    }


}
