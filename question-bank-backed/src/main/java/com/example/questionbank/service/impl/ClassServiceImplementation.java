package com.example.questionbank.service.impl;

import com.example.questionbank.dto.request.CreateClassRequestDTO;
import com.example.questionbank.dto.request.UpdateClassRequestDTO;
import com.example.questionbank.dto.response.ClassResponseDTO;
import com.example.questionbank.dto.response.ClassSummaryDTO;
import com.example.questionbank.exception.RecordNotFoundException;
import com.example.questionbank.mapper.ClassMapper;
import com.example.questionbank.model.ClassEntity;
import com.example.questionbank.repository.ClassRepository;
import com.example.questionbank.service.ClassService;
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
public class ClassServiceImplementation implements ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private ClassMapper classMapper;

    @Override
    public ClassResponseDTO createClass(CreateClassRequestDTO createClassRequestDTO) {
        log.info("Creating new class with name: {}", createClassRequestDTO.getName());

        // Check if class name already exists
        if (classRepository.existsByNameIgnoreCase(createClassRequestDTO.getName())) {
            throw new IllegalArgumentException("Class with name '" + createClassRequestDTO.getName() + "' already exists");
        }

        try {
            ClassEntity classEntity = classMapper.toEntity(createClassRequestDTO);
            ClassEntity savedClass = classRepository.save(classEntity);

            log.info("Successfully created class with ID: {}", savedClass.getId());
            return classMapper.toResponseDTO(savedClass);

        } catch (Exception e) {
            log.error("Failed to create class with name: {}", createClassRequestDTO.getName(), e);
            throw new RuntimeException("Failed to create class", e);
        }
    }

    @Override
    public ClassResponseDTO updateClass(Long id, UpdateClassRequestDTO updateClassRequestDTO) {
        log.info("Updating class with ID: {}", id);

        ClassEntity existingClass = classRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Class not found with id: " + id));

        // Check if name is being changed and if new name already exists
        if (!existingClass.getName().equalsIgnoreCase(updateClassRequestDTO.getName()) &&
                classRepository.existsByNameIgnoreCase(updateClassRequestDTO.getName())) {
            throw new IllegalArgumentException("Class with name '" + updateClassRequestDTO.getName() + "' already exists");
        }

        try {
            classMapper.updateEntityFromDTO(updateClassRequestDTO, existingClass);
            ClassEntity updatedClass = classRepository.save(existingClass);

            log.info("Successfully updated class with ID: {}", id);
            return classMapper.toResponseDTO(updatedClass);

        } catch (Exception e) {
            log.error("Failed to update class with ID: {}", id, e);
            throw new RuntimeException("Failed to update class", e);
        }
    }

    @Override
    public void deleteClass(Long id) {
        log.info("Deleting class with ID: {}", id);

        ClassEntity classEntity = classRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Class not found with id: " + id));

        try {
            // Soft delete by setting isActive to false
            classEntity.setIsActive(false);
            classRepository.save(classEntity);

            log.info("Successfully soft deleted class with ID: {}", id);

        } catch (Exception e) {
            log.error("Failed to delete class with ID: {}", id, e);
            throw new RuntimeException("Failed to delete class", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ClassResponseDTO getClassById(Long id) {
        log.debug("Fetching class with ID: {}", id);

        ClassEntity classEntity = classRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Class not found with id: " + id));

        return classMapper.toResponseDTO(classEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClassResponseDTO> getAllClassesWithPagination(Pageable pageable) {
        log.debug("Fetching all classes with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<ClassEntity> classEntities = classRepository.findAll(pageable);
        return classEntities.map(classMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClassResponseDTO> searchClasses(String name, Pageable pageable) {
        log.debug("Searching classes with name containing: {}", name);

        Page<ClassEntity> classEntities = classRepository.findByNameContainingIgnoreCase(name, pageable);
        return classEntities.map(classMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassResponseDTO> getAllClasses() {
        log.debug("Fetching all classes");

        List<ClassEntity> classEntities = classRepository.findAll();
        return classMapper.toResponseDTOList(classEntities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassResponseDTO> getAllActiveClasses() {
        log.debug("Fetching all active classes");

        List<ClassEntity> activeClasses = classRepository.findByIsActiveTrue();
        return classMapper.toResponseDTOList(activeClasses);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClassResponseDTO> getAllActiveClassesWithPagination(Pageable pageable) {
        log.debug("Fetching all active classes with pagination");

        Page<ClassEntity> activeClasses = classRepository.findByIsActiveTrue(pageable);
        return activeClasses.map(classMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassSummaryDTO> getAllActiveClassesSummary() {
        log.debug("Fetching all active classes summary");

        List<ClassEntity> activeClasses = classRepository.findActiveClassesOrderByName();
        return classMapper.toSummaryDTOList(activeClasses);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return classRepository.existsByNameIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassSummaryDTO getClassSummaryById(Long id) {
        ClassEntity classEntity = classRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Class not found with id: " + id));

        return classMapper.toSummaryDTO(classEntity);
    }
}