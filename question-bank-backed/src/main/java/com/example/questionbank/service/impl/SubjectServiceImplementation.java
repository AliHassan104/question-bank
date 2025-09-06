package com.example.questionbank.service.impl;

import com.example.questionbank.dto.request.CreateSubjectRequestDTO;
import com.example.questionbank.dto.request.UpdateSubjectRequestDTO;
import com.example.questionbank.dto.response.SubjectResponseDTO;
import com.example.questionbank.exception.DuplicateResourceException;
import com.example.questionbank.exception.RecordNotFoundException;
import com.example.questionbank.mapper.SubjectMapper;
import com.example.questionbank.model.ClassEntity;
import com.example.questionbank.model.Subject;
import com.example.questionbank.repository.ClassRepository;
import com.example.questionbank.repository.SubjectRepository;
import com.example.questionbank.service.SubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class SubjectServiceImplementation implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SubjectMapper subjectMapper;

    @Override
    public SubjectResponseDTO createSubject(CreateSubjectRequestDTO dto) {
        log.info("Creating new subject with name: {} for class ID: {}", dto.getName(), dto.getClassId());

        // Validate class exists
        ClassEntity classEntity = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new RecordNotFoundException("Class", "id", dto.getClassId()));

        // Check if subject name already exists in this class
        if (subjectRepository.existsByNameIgnoreCaseAndClassEntityId(dto.getName(), dto.getClassId())) {
            throw new DuplicateResourceException("Subject", "name", dto.getName() + " in class " + classEntity.getName());
        }

        try {
            Subject subject = subjectMapper.toEntity(dto);
            subject.setClassEntity(classEntity);
            Subject savedSubject = subjectRepository.save(subject);

            log.info("Successfully created subject with ID: {}", savedSubject.getId());
            return subjectMapper.toResponseDTO(savedSubject);

        } catch (Exception e) {
            log.error("Failed to create subject: {}", dto.getName(), e);
            throw new RuntimeException("Failed to create subject", e);
        }
    }

    @Override
    public SubjectResponseDTO updateSubject(Long id, UpdateSubjectRequestDTO dto) {
        log.info("Updating subject with ID: {}", id);

        Subject existingSubject = subjectRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Subject", "id", id));

        // Validate class exists
        ClassEntity classEntity = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new RecordNotFoundException("Class", "id", dto.getClassId()));

        // Check if name is being changed and if new name already exists in the class
        if (!existingSubject.getName().equalsIgnoreCase(dto.getName()) ||
                !existingSubject.getClassEntity().getId().equals(dto.getClassId())) {

            if (subjectRepository.existsByNameIgnoreCaseAndClassEntityId(dto.getName(), dto.getClassId())) {
                throw new DuplicateResourceException("Subject", "name", dto.getName() + " in class " + classEntity.getName());
            }
        }

        try {
            existingSubject.setName(dto.getName());
            existingSubject.setDescription(dto.getDescription());
            existingSubject.setClassEntity(classEntity);
            if (dto.getIsActive() != null) {
                existingSubject.setIsActive(dto.getIsActive());
            }

            Subject updatedSubject = subjectRepository.save(existingSubject);
            log.info("Successfully updated subject with ID: {}", id);
            return subjectMapper.toResponseDTO(updatedSubject);

        } catch (Exception e) {
            log.error("Failed to update subject with ID: {}", id, e);
            throw new RuntimeException("Failed to update subject", e);
        }
    }

    @Override
    public void deleteSubject(Long id) {
        log.info("Deleting subject with ID: {}", id);

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Subject", "id", id));

        try {
            // Soft delete
            subject.setIsActive(false);
            subjectRepository.save(subject);
            log.info("Successfully soft deleted subject with ID: {}", id);

        } catch (Exception e) {
            log.error("Failed to delete subject with ID: {}", id, e);
            throw new RuntimeException("Failed to delete subject", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponseDTO getSubjectById(Long id) {
        log.debug("Fetching subject with ID: {}", id);

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Subject", "id", id));

        return subjectMapper.toResponseDTO(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponseDTO> getAllSubjects() {
        log.debug("Fetching all subjects");
        List<Subject> subjects = subjectRepository.findAll();
        return subjectMapper.toResponseDTOList(subjects);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubjectResponseDTO> getAllSubjectsWithPagination(Pageable pageable) {
        log.debug("Fetching subjects with pagination");
        Page<Subject> subjects = subjectRepository.findAll(pageable);
        return subjects.map(subjectMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubjectResponseDTO> searchSubjects(String name, Pageable pageable) {
        log.debug("Searching subjects with name: {}", name);
        Page<Subject> subjects = subjectRepository.findByNameContainingIgnoreCase(name, pageable);
        return subjects.map(subjectMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponseDTO> getSubjectsByClass(Long classId) {
        log.debug("Fetching subjects for class ID: {}", classId);

        // Validate class exists
        classRepository.findById(classId)
                .orElseThrow(() -> new RecordNotFoundException("Class", "id", classId));

        List<Subject> subjects = subjectRepository.findByClassEntityIdAndIsActiveTrue(classId);
        return subjectMapper.toResponseDTOList(subjects);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponseDTO> getAllActiveSubjects() {
        log.debug("Fetching all active subjects");
        List<Subject> activeSubjects = subjectRepository.findByIsActiveTrue();
        return subjectMapper.toResponseDTOList(activeSubjects);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndClass(String name, Long classId) {
        return subjectRepository.existsByNameIgnoreCaseAndClassEntityId(name, classId);
    }
}