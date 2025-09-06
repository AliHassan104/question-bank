package com.example.questionbank.service;

import com.example.questionbank.dto.request.CreateClassRequestDTO;
import com.example.questionbank.dto.request.UpdateClassRequestDTO;
import com.example.questionbank.dto.response.ClassResponseDTO;
import com.example.questionbank.dto.response.ClassSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClassService {

    // CRUD operations with DTOs
    ClassResponseDTO createClass(CreateClassRequestDTO createClassRequestDTO);
    ClassResponseDTO updateClass(Long id, UpdateClassRequestDTO updateClassRequestDTO);
    void deleteClass(Long id);
    ClassResponseDTO getClassById(Long id);

    // Pagination and searching with DTOs
    Page<ClassResponseDTO> getAllClassesWithPagination(Pageable pageable);
    Page<ClassResponseDTO> searchClasses(String name, Pageable pageable);
    List<ClassResponseDTO> getAllClasses();

    // Active classes only
    List<ClassResponseDTO> getAllActiveClasses();
    Page<ClassResponseDTO> getAllActiveClassesWithPagination(Pageable pageable);

    // Summary DTOs for dropdowns/selections
    List<ClassSummaryDTO> getAllActiveClassesSummary();

    // Utility methods
    boolean existsByName(String name);
    ClassSummaryDTO getClassSummaryById(Long id);
}