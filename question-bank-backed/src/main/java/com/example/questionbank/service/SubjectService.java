package com.example.questionbank.service;

import com.example.questionbank.dto.request.CreateSubjectRequestDTO;
import com.example.questionbank.dto.request.UpdateSubjectRequestDTO;
import com.example.questionbank.dto.response.SubjectResponseDTO;
import com.example.questionbank.model.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubjectService {
    SubjectResponseDTO createSubject(CreateSubjectRequestDTO dto);
    SubjectResponseDTO updateSubject(Long id, UpdateSubjectRequestDTO dto);
    void deleteSubject(Long id);
    SubjectResponseDTO getSubjectById(Long id);

    List<SubjectResponseDTO> getAllSubjects();
    Page<SubjectResponseDTO> getAllSubjectsWithPagination(Pageable pageable);
    Page<SubjectResponseDTO> searchSubjects(String name, Pageable pageable);

    List<SubjectResponseDTO> getSubjectsByClass(Long classId);
    List<SubjectResponseDTO> getAllActiveSubjects();

    boolean existsByNameAndClass(String name, Long classId);
}
