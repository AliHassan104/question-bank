package com.example.questionbank.service.impl;

import com.example.questionbank.dto.request.CreateMCQOptionRequestDTO;
import com.example.questionbank.dto.request.UpdateMCQOptionRequestDTO;
import com.example.questionbank.dto.response.MCQOptionResponseDTO;
import com.example.questionbank.exception.RecordNotFoundException;
import com.example.questionbank.exception.ValidationException;
import com.example.questionbank.mapper.MCQOptionMapper;
import com.example.questionbank.model.MCQOption;
import com.example.questionbank.model.Question;
import com.example.questionbank.model.enums.QuestionType;
import com.example.questionbank.repository.MCQOptionRepository;
import com.example.questionbank.repository.QuestionRepository;
import com.example.questionbank.service.MCQOptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class MCQOptionServiceImplementation implements MCQOptionService {

    @Autowired
    private MCQOptionRepository mcqOptionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private MCQOptionMapper mcqOptionMapper;

    @Override
    public MCQOptionResponseDTO createMCQOption(CreateMCQOptionRequestDTO dto) {
        log.info("Creating MCQ option: {}", dto.getOptionText());

        try {
            MCQOption mcqOption = mcqOptionMapper.toEntity(dto);
            MCQOption savedOption = mcqOptionRepository.save(mcqOption);

            log.info("Successfully created MCQ option with ID: {}", savedOption.getId());
            return mcqOptionMapper.toResponseDTO(savedOption);

        } catch (Exception e) {
            log.error("Failed to create MCQ option: {}", dto.getOptionText(), e);
            throw new RuntimeException("Failed to create MCQ option", e);
        }
    }

    @Override
    public MCQOptionResponseDTO updateMCQOption(Long id, UpdateMCQOptionRequestDTO dto) {
        log.info("Updating MCQ option with ID: {}", id);

        MCQOption existingOption = mcqOptionRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("MCQOption", "id", id));

        try {
            mcqOptionMapper.updateEntityFromDTO(dto, existingOption);
            MCQOption updatedOption = mcqOptionRepository.save(existingOption);

            log.info("Successfully updated MCQ option with ID: {}", id);
            return mcqOptionMapper.toResponseDTO(updatedOption);

        } catch (Exception e) {
            log.error("Failed to update MCQ option with ID: {}", id, e);
            throw new RuntimeException("Failed to update MCQ option", e);
        }
    }

    @Override
    public void deleteMCQOption(Long id) {
        log.info("Deleting MCQ option with ID: {}", id);

        MCQOption option = mcqOptionRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("MCQOption", "id", id));

        try {
            // Soft delete
            option.setIsActive(false);
            mcqOptionRepository.save(option);
            log.info("Successfully soft deleted MCQ option with ID: {}", id);

        } catch (Exception e) {
            log.error("Failed to delete MCQ option with ID: {}", id, e);
            throw new RuntimeException("Failed to delete MCQ option", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MCQOptionResponseDTO getMCQOptionById(Long id) {
        log.debug("Fetching MCQ option with ID: {}", id);

        MCQOption option = mcqOptionRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("MCQOption", "id", id));

        return mcqOptionMapper.toResponseDTO(option);
    }

    @Override
    public List<MCQOptionResponseDTO> createMCQOptions(List<CreateMCQOptionRequestDTO> dtos) {
        log.info("Creating {} MCQ options", dtos.size());

        try {
            List<MCQOption> options = mcqOptionMapper.toEntityList(dtos);
            List<MCQOption> savedOptions = mcqOptionRepository.saveAll(options);

            log.info("Successfully created {} MCQ options", savedOptions.size());
            return mcqOptionMapper.toResponseDTOList(savedOptions);

        } catch (Exception e) {
            log.error("Failed to create MCQ options", e);
            throw new RuntimeException("Failed to create MCQ options", e);
        }
    }

    @Override
    public List<MCQOptionResponseDTO> updateMCQOptions(List<UpdateMCQOptionRequestDTO> dtos) {
        log.info("Updating {} MCQ options", dtos.size());

        List<MCQOption> updatedOptions = new ArrayList<>();

        try {
            for (UpdateMCQOptionRequestDTO dto : dtos) {
                if (dto.getId() != null) {
                    MCQOption existingOption = mcqOptionRepository.findById(dto.getId())
                            .orElseThrow(() -> new RecordNotFoundException("MCQOption", "id", dto.getId()));

                    mcqOptionMapper.updateEntityFromDTO(dto, existingOption);
                    updatedOptions.add(existingOption);
                }
            }

            List<MCQOption> savedOptions = mcqOptionRepository.saveAll(updatedOptions);
            log.info("Successfully updated {} MCQ options", savedOptions.size());
            return mcqOptionMapper.toResponseDTOList(savedOptions);

        } catch (Exception e) {
            log.error("Failed to update MCQ options", e);
            throw new RuntimeException("Failed to update MCQ options", e);
        }
    }

    @Override
    public void deleteMCQOptionsByIds(List<Long> ids) {
        log.info("Deleting {} MCQ options", ids.size());

        try {
            List<MCQOption> options = mcqOptionRepository.findAllById(ids);
            options.forEach(option -> option.setIsActive(false));
            mcqOptionRepository.saveAll(options);

            log.info("Successfully deleted {} MCQ options", options.size());

        } catch (Exception e) {
            log.error("Failed to delete MCQ options", e);
            throw new RuntimeException("Failed to delete MCQ options", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MCQOptionResponseDTO> getOptionsByQuestionId(Long questionId) {
        log.debug("Fetching options for question ID: {}", questionId);

        List<MCQOption> options = mcqOptionRepository.findByQuestionIdOrderByOptionOrderAsc(questionId);
        return mcqOptionMapper.toResponseDTOList(options);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MCQOptionResponseDTO> getActiveOptionsByQuestionId(Long questionId) {
        log.debug("Fetching active options for question ID: {}", questionId);

        List<MCQOption> options = mcqOptionRepository.findByQuestionIdAndIsActiveTrueOrderByOptionOrderAsc(questionId);
        return mcqOptionMapper.toResponseDTOList(options);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MCQOptionResponseDTO> getCorrectOptionsByQuestionId(Long questionId) {
        log.debug("Fetching correct options for question ID: {}", questionId);

        List<MCQOption> options = mcqOptionRepository.findByQuestionIdAndIsCorrectTrueAndIsActiveTrue(questionId);
        return mcqOptionMapper.toResponseDTOList(options);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<MCQOptionResponseDTO>> getOptionsByMultipleQuestionIds(List<Long> questionIds) {
        log.debug("Fetching options for {} questions", questionIds.size());

        List<MCQOption> options = mcqOptionRepository.findByQuestionIdIn(questionIds);

        Map<Long, List<MCQOption>> optionsMap = options.stream()
                .collect(Collectors.groupingBy(option -> option.getQuestion().getId()));

        return optionsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> mcqOptionMapper.toResponseDTOList(entry.getValue())
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<MCQOptionResponseDTO>> getActiveOptionsByMultipleQuestionIds(List<Long> questionIds) {
        log.debug("Fetching active options for {} questions", questionIds.size());

        List<MCQOption> options = mcqOptionRepository.findByQuestionIdInAndIsActiveTrue(questionIds);

        Map<Long, List<MCQOption>> optionsMap = options.stream()
                .collect(Collectors.groupingBy(option -> option.getQuestion().getId()));

        return optionsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> mcqOptionMapper.toResponseDTOList(entry.getValue())
                ));
    }

    @Override
    public List<MCQOptionResponseDTO> createOptionsForQuestion(Long questionId, List<CreateMCQOptionRequestDTO> dtos) {
        log.info("Creating {} options for question ID: {}", dtos.size(), questionId);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", questionId));

        validateOptionsForQuestion(questionId, dtos);

        try {
            List<MCQOption> options = mcqOptionMapper.toEntityList(dtos);
            options.forEach(option -> option.setQuestion(question));

            List<MCQOption> savedOptions = mcqOptionRepository.saveAll(options);
            log.info("Successfully created {} options for question ID: {}", savedOptions.size(), questionId);

            return mcqOptionMapper.toResponseDTOList(savedOptions);

        } catch (Exception e) {
            log.error("Failed to create options for question ID: {}", questionId, e);
            throw new RuntimeException("Failed to create options for question", e);
        }
    }

    @Override
    public List<MCQOptionResponseDTO> updateOptionsForQuestion(Long questionId, List<UpdateMCQOptionRequestDTO> dtos) {
        log.info("Updating options for question ID: {}", questionId);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", questionId));

        validateOptionsForQuestionUpdate(questionId, dtos);

        try {
            List<MCQOption> updatedOptions = new ArrayList<>();
            List<MCQOption> newOptions = new ArrayList<>();

            for (UpdateMCQOptionRequestDTO dto : dtos) {
                if (dto.getId() != null) {
                    // Update existing option
                    MCQOption existingOption = mcqOptionRepository.findById(dto.getId())
                            .orElseThrow(() -> new RecordNotFoundException("MCQOption", "id", dto.getId()));

                    mcqOptionMapper.updateEntityFromDTO(dto, existingOption);
                    updatedOptions.add(existingOption);
                } else {
                    // Create new option
                    MCQOption newOption = mcqOptionMapper.toEntityFromUpdateDTO(dto);
                    newOption.setQuestion(question);
                    newOptions.add(newOption);
                }
            }

            List<MCQOption> allSavedOptions = new ArrayList<>();
            if (!updatedOptions.isEmpty()) {
                allSavedOptions.addAll(mcqOptionRepository.saveAll(updatedOptions));
            }
            if (!newOptions.isEmpty()) {
                allSavedOptions.addAll(mcqOptionRepository.saveAll(newOptions));
            }

            log.info("Successfully updated options for question ID: {}", questionId);
            return mcqOptionMapper.toResponseDTOList(allSavedOptions);

        } catch (Exception e) {
            log.error("Failed to update options for question ID: {}", questionId, e);
            throw new RuntimeException("Failed to update options for question", e);
        }
    }

    @Override
    public void deleteAllOptionsByQuestionId(Long questionId) {
        log.info("Deleting all options for question ID: {}", questionId);

        try {
            List<MCQOption> options = mcqOptionRepository.findByQuestionId(questionId);
            options.forEach(option -> option.setIsActive(false));
            mcqOptionRepository.saveAll(options);

            log.info("Successfully deleted all options for question ID: {}", questionId);

        } catch (Exception e) {
            log.error("Failed to delete options for question ID: {}", questionId, e);
            throw new RuntimeException("Failed to delete options for question", e);
        }
    }

    @Override
    public void replaceAllOptionsForQuestion(Long questionId, List<CreateMCQOptionRequestDTO> newOptions) {
        log.info("Replacing all options for question ID: {}", questionId);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", questionId));

        validateOptionsForQuestion(questionId, newOptions);

        try {
            // Deactivate existing options
            deleteAllOptionsByQuestionId(questionId);

            // Create new options
            createOptionsForQuestion(questionId, newOptions);

            log.info("Successfully replaced all options for question ID: {}", questionId);

        } catch (Exception e) {
            log.error("Failed to replace options for question ID: {}", questionId, e);
            throw new RuntimeException("Failed to replace options for question", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MCQOptionResponseDTO> getAllMCQOptions(Pageable pageable) {
        log.debug("Fetching MCQ options with pagination");

        Page<MCQOption> options = mcqOptionRepository.findAll(pageable);
        return options.map(mcqOptionMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MCQOptionResponseDTO> searchMCQOptions(String optionText, Pageable pageable) {
        log.debug("Searching MCQ options with text: {}", optionText);

        Page<MCQOption> options = mcqOptionRepository.findByOptionTextContainingIgnoreCase(optionText, pageable);
        return options.map(mcqOptionMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MCQOptionResponseDTO> getOptionsByQuestion(Long questionId, Pageable pageable) {
        log.debug("Fetching options for question ID: {} with pagination", questionId);

        // Note: This would require a custom repository method
        Page<MCQOption> options = mcqOptionRepository.findAll(pageable);
        return options.map(mcqOptionMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasCorrectOption(Long questionId) {
        return mcqOptionRepository.hasCorrectOption(questionId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countOptionsByQuestion(Long questionId) {
        return mcqOptionRepository.countActiveOptionsByQuestion(questionId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countCorrectOptionsByQuestion(Long questionId) {
        return mcqOptionRepository.countCorrectOptionsByQuestion(questionId);
    }

    @Override
    public void validateOptionsForQuestion(Long questionId, List<CreateMCQOptionRequestDTO> options) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", questionId));

        Map<String, String> errors = new HashMap<>();

        // Validate minimum options for MCQ
        if (question.isMCQType() && (options == null || options.size() < 2)) {
            errors.put("mcqOptions", "MCQ questions must have at least 2 options");
        }

        // Validate correct options
        if (options != null) {
            long correctCount = options.stream()
                    .mapToLong(opt -> Boolean.TRUE.equals(opt.getIsCorrect()) ? 1 : 0)
                    .sum();

            if (question.getQuestionType() == QuestionType.SINGLE_CHOICE && correctCount != 1) {
                errors.put("correctOptions", "Single choice questions must have exactly 1 correct option");
            } else if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE && correctCount < 1) {
                errors.put("correctOptions", "Multiple choice questions must have at least 1 correct option");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("MCQ option validation failed", errors);
        }
    }

    @Override
    public void validateOptionsForQuestionUpdate(Long questionId, List<UpdateMCQOptionRequestDTO> options) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", questionId));

        Map<String, String> errors = new HashMap<>();

        if (question.isMCQType() && (options == null || options.size() < 2)) {
            errors.put("mcqOptions", "MCQ questions must have at least 2 options");
        }

        if (options != null) {
            long correctCount = options.stream()
                    .mapToLong(opt -> Boolean.TRUE.equals(opt.getIsCorrect()) ? 1 : 0)
                    .sum();

            if (question.getQuestionType() == QuestionType.SINGLE_CHOICE && correctCount != 1) {
                errors.put("correctOptions", "Single choice questions must have exactly 1 correct option");
            } else if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE && correctCount < 1) {
                errors.put("correctOptions", "Multiple choice questions must have at least 1 correct option");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("MCQ option validation failed", errors);
        }
    }

    @Override
    public void markOptionAsCorrect(Long optionId) {
        log.info("Marking option as correct: {}", optionId);

        MCQOption option = mcqOptionRepository.findById(optionId)
                .orElseThrow(() -> new RecordNotFoundException("MCQOption", "id", optionId));

        option.setIsCorrect(true);
        mcqOptionRepository.save(option);
    }

    @Override
    public void markOptionAsIncorrect(Long optionId) {
        log.info("Marking option as incorrect: {}", optionId);

        MCQOption option = mcqOptionRepository.findById(optionId)
                .orElseThrow(() -> new RecordNotFoundException("MCQOption", "id", optionId));

        option.setIsCorrect(false);
        mcqOptionRepository.save(option);
    }

    @Override
    public void setCorrectOption(Long questionId, Long optionId) {
        log.info("Setting correct option {} for question {}", optionId, questionId);

        // First, mark all options as incorrect
        List<MCQOption> allOptions = mcqOptionRepository.findByQuestionIdAndIsActiveTrue(questionId);
        allOptions.forEach(opt -> opt.setIsCorrect(false));
        mcqOptionRepository.saveAll(allOptions);

        // Then mark the specified option as correct
        markOptionAsCorrect(optionId);
    }

    @Override
    public void setMultipleCorrectOptions(Long questionId, List<Long> optionIds) {
        log.info("Setting multiple correct options for question {}: {}", questionId, optionIds);

        // First, mark all options as incorrect
        List<MCQOption> allOptions = mcqOptionRepository.findByQuestionIdAndIsActiveTrue(questionId);
        allOptions.forEach(opt -> opt.setIsCorrect(false));
        mcqOptionRepository.saveAll(allOptions);

        // Then mark specified options as correct
        for (Long optionId : optionIds) {
            markOptionAsCorrect(optionId);
        }
    }

    @Override
    public void reorderOptions(Long questionId, Map<Long, Integer> optionOrderMap) {
        log.info("Reordering options for question {}", questionId);

        List<MCQOption> options = mcqOptionRepository.findByQuestionIdAndIsActiveTrue(questionId);

        for (MCQOption option : options) {
            Integer newOrder = optionOrderMap.get(option.getId());
            if (newOrder != null) {
                option.setOptionOrder(newOrder);
            }
        }

        mcqOptionRepository.saveAll(options);
    }

    @Override
    public void activateOption(Long optionId) {
        MCQOption option = mcqOptionRepository.findById(optionId)
                .orElseThrow(() -> new RecordNotFoundException("MCQOption", "id", optionId));
        option.setIsActive(true);
        mcqOptionRepository.save(option);
    }

    @Override
    public void deactivateOption(Long optionId) {
        MCQOption option = mcqOptionRepository.findById(optionId)
                .orElseThrow(() -> new RecordNotFoundException("MCQOption", "id", optionId));
        option.setIsActive(false);
        mcqOptionRepository.save(option);
    }

    @Override
    public void activateAllOptionsForQuestion(Long questionId) {
        List<MCQOption> options = mcqOptionRepository.findByQuestionId(questionId);
        options.forEach(opt -> opt.setIsActive(true));
        mcqOptionRepository.saveAll(options);
    }

    @Override
    public void deactivateAllOptionsForQuestion(Long questionId) {
        List<MCQOption> options = mcqOptionRepository.findByQuestionId(questionId);
        options.forEach(opt -> opt.setIsActive(false));
        mcqOptionRepository.saveAll(options);
    }
}