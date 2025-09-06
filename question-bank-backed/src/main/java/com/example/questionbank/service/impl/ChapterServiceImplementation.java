package com.example.questionbank.service.impl;

import com.example.questionbank.dto.request.CreateChapterRequestDTO;
import com.example.questionbank.dto.request.UpdateChapterRequestDTO;
import com.example.questionbank.dto.response.ChapterResponseDTO;
import com.example.questionbank.exception.DuplicateResourceException;
import com.example.questionbank.exception.RecordNotFoundException;
import com.example.questionbank.mapper.ChapterMapper;
import com.example.questionbank.model.Chapter;
import com.example.questionbank.model.Subject;
import com.example.questionbank.repository.ChapterRepository;
import com.example.questionbank.repository.SubjectRepository;
import com.example.questionbank.service.ChapterService;
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
public class ChapterServiceImplementation implements ChapterService {

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ChapterMapper chapterMapper;

    @Override
    public ChapterResponseDTO createChapter(CreateChapterRequestDTO dto) {
        log.info("Creating new chapter with name: {} for subject ID: {}", dto.getName(), dto.getSubjectId());

        // Validate subject exists
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new RecordNotFoundException("Subject", "id", dto.getSubjectId()));

        // Check if chapter name already exists in this subject
        if (chapterRepository.existsByNameIgnoreCaseAndSubjectId(dto.getName(), dto.getSubjectId())) {
            throw new DuplicateResourceException("Chapter", "name",
                    dto.getName() + " in subject " + subject.getName());
        }

        try {
            Chapter chapter = chapterMapper.toEntity(dto);
            chapter.setSubject(subject);
            Chapter savedChapter = chapterRepository.save(chapter);

            log.info("Successfully created chapter with ID: {}", savedChapter.getId());
            return chapterMapper.toResponseDTO(savedChapter);

        } catch (Exception e) {
            log.error("Failed to create chapter: {}", dto.getName(), e);
            throw new RuntimeException("Failed to create chapter", e);
        }
    }

    @Override
    public ChapterResponseDTO updateChapter(Long id, UpdateChapterRequestDTO dto) {
        log.info("Updating chapter with ID: {}", id);

        Chapter existingChapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Chapter", "id", id));

        // Validate subject exists
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new RecordNotFoundException("Subject", "id", dto.getSubjectId()));

        // Check if name is being changed and if new name already exists in the subject
        if (!existingChapter.getName().equalsIgnoreCase(dto.getName()) ||
                !existingChapter.getSubject().getId().equals(dto.getSubjectId())) {

            if (chapterRepository.existsByNameIgnoreCaseAndSubjectId(dto.getName(), dto.getSubjectId())) {
                throw new DuplicateResourceException("Chapter", "name",
                        dto.getName() + " in subject " + subject.getName());
            }
        }

        try {
            chapterMapper.updateEntityFromDTO(dto, existingChapter);
            existingChapter.setSubject(subject);

            Chapter updatedChapter = chapterRepository.save(existingChapter);
            log.info("Successfully updated chapter with ID: {}", id);
            return chapterMapper.toResponseDTO(updatedChapter);

        } catch (Exception e) {
            log.error("Failed to update chapter with ID: {}", id, e);
            throw new RuntimeException("Failed to update chapter", e);
        }
    }

    @Override
    public void deleteChapter(Long id) {
        log.info("Deleting chapter with ID: {}", id);

        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Chapter", "id", id));

        try {
            // Soft delete
            chapter.setIsActive(false);
            chapterRepository.save(chapter);
            log.info("Successfully soft deleted chapter with ID: {}", id);

        } catch (Exception e) {
            log.error("Failed to delete chapter with ID: {}", id, e);
            throw new RuntimeException("Failed to delete chapter", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ChapterResponseDTO getChapterById(Long id) {
        log.debug("Fetching chapter with ID: {}", id);

        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Chapter", "id", id));

        return chapterMapper.toResponseDTO(chapter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponseDTO> getAllChapters() {
        log.debug("Fetching all chapters");
        List<Chapter> chapters = chapterRepository.findAll();
        return chapterMapper.toResponseDTOList(chapters);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChapterResponseDTO> getAllChaptersWithPagination(Pageable pageable) {
        log.debug("Fetching chapters with pagination");
        Page<Chapter> chapters = chapterRepository.findAll(pageable);
        return chapters.map(chapterMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChapterResponseDTO> searchChapters(String name, Pageable pageable) {
        log.debug("Searching chapters with name: {}", name);
        Page<Chapter> chapters = chapterRepository.findByNameContainingIgnoreCase(name, pageable);
        return chapters.map(chapterMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponseDTO> getChaptersBySubject(Long subjectId) {
        log.debug("Fetching chapters for subject ID: {}", subjectId);

        // Validate subject exists
        subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RecordNotFoundException("Subject", "id", subjectId));

        List<Chapter> chapters = chapterRepository.findBySubjectIdAndIsActiveTrue(subjectId);
        return chapterMapper.toResponseDTOList(chapters);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponseDTO> getChaptersByClass(Long classId) {
        log.debug("Fetching chapters for class ID: {}", classId);

        List<Chapter> chapters = chapterRepository.findBySubjectClassEntityIdAndIsActiveTrue(classId);
        return chapterMapper.toResponseDTOList(chapters);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponseDTO> getAllActiveChapters() {
        log.debug("Fetching all active chapters");
        List<Chapter> activeChapters = chapterRepository.findByIsActiveTrue();
        return chapterMapper.toResponseDTOList(activeChapters);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponseDTO> filterChapters(Long subjectId, Long classId) {
        log.debug("Filtering chapters with subjectId: {} and classId: {}", subjectId, classId);
        List<Chapter> chapters = chapterRepository.findActiveChaptersBySubjectAndClass(subjectId, classId);
        return chapterMapper.toResponseDTOList(chapters);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndSubject(String name, Long subjectId) {
        return chapterRepository.existsByNameIgnoreCaseAndSubjectId(name, subjectId);
    }
}