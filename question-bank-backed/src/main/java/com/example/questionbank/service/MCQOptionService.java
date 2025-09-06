package com.example.questionbank.service;

import com.example.questionbank.dto.request.CreateMCQOptionRequestDTO;
import com.example.questionbank.dto.request.UpdateMCQOptionRequestDTO;
import com.example.questionbank.dto.response.MCQOptionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface MCQOptionService {

    // Basic CRUD operations
    MCQOptionResponseDTO createMCQOption(CreateMCQOptionRequestDTO dto);
    MCQOptionResponseDTO updateMCQOption(Long id, UpdateMCQOptionRequestDTO dto);
    void deleteMCQOption(Long id);
    MCQOptionResponseDTO getMCQOptionById(Long id);

    // Bulk operations
    List<MCQOptionResponseDTO> createMCQOptions(List<CreateMCQOptionRequestDTO> dtos);
    List<MCQOptionResponseDTO> updateMCQOptions(List<UpdateMCQOptionRequestDTO> dtos);
    void deleteMCQOptionsByIds(List<Long> ids);

    // Question-based operations
    List<MCQOptionResponseDTO> getOptionsByQuestionId(Long questionId);
    List<MCQOptionResponseDTO> getActiveOptionsByQuestionId(Long questionId);
    List<MCQOptionResponseDTO> getCorrectOptionsByQuestionId(Long questionId);
    Map<Long, List<MCQOptionResponseDTO>> getOptionsByMultipleQuestionIds(List<Long> questionIds);
    Map<Long, List<MCQOptionResponseDTO>> getActiveOptionsByMultipleQuestionIds(List<Long> questionIds);

    // Question management operations
    List<MCQOptionResponseDTO> createOptionsForQuestion(Long questionId, List<CreateMCQOptionRequestDTO> dtos);
    List<MCQOptionResponseDTO> updateOptionsForQuestion(Long questionId, List<UpdateMCQOptionRequestDTO> dtos);
    void deleteAllOptionsByQuestionId(Long questionId);
    void replaceAllOptionsForQuestion(Long questionId, List<CreateMCQOptionRequestDTO> newOptions);

    // Pagination and searching
    Page<MCQOptionResponseDTO> getAllMCQOptions(Pageable pageable);
    Page<MCQOptionResponseDTO> searchMCQOptions(String optionText, Pageable pageable);
    Page<MCQOptionResponseDTO> getOptionsByQuestion(Long questionId, Pageable pageable);

    // Validation and utility methods
    boolean hasCorrectOption(Long questionId);
    Long countOptionsByQuestion(Long questionId);
    Long countCorrectOptionsByQuestion(Long questionId);
    void validateOptionsForQuestion(Long questionId, List<CreateMCQOptionRequestDTO> options);
    void validateOptionsForQuestionUpdate(Long questionId, List<UpdateMCQOptionRequestDTO> options);

    // Business logic methods
    void markOptionAsCorrect(Long optionId);
    void markOptionAsIncorrect(Long optionId);
    void setCorrectOption(Long questionId, Long optionId);
    void setMultipleCorrectOptions(Long questionId, List<Long> optionIds);
    void reorderOptions(Long questionId, Map<Long, Integer> optionOrderMap);

    // Activation/Deactivation
    void activateOption(Long optionId);
    void deactivateOption(Long optionId);
    void activateAllOptionsForQuestion(Long questionId);
    void deactivateAllOptionsForQuestion(Long questionId);
}