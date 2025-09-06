package com.example.questionbank.service;

import com.example.questionbank.dto.request.CreateChapterRequestDTO;
import com.example.questionbank.dto.request.UpdateChapterRequestDTO;
import com.example.questionbank.dto.response.ChapterResponseDTO;
import com.example.questionbank.model.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChapterService {
    ChapterResponseDTO createChapter(CreateChapterRequestDTO dto);
    ChapterResponseDTO updateChapter(Long id, UpdateChapterRequestDTO dto);
    void deleteChapter(Long id);
    ChapterResponseDTO getChapterById(Long id);

    List<ChapterResponseDTO> getAllChapters();
    Page<ChapterResponseDTO> getAllChaptersWithPagination(Pageable pageable);
    Page<ChapterResponseDTO> searchChapters(String name, Pageable pageable);

    List<ChapterResponseDTO> getChaptersBySubject(Long subjectId);
    List<ChapterResponseDTO> getChaptersByClass(Long classId);
    List<ChapterResponseDTO> getAllActiveChapters();
    List<ChapterResponseDTO> filterChapters(Long subjectId, Long classId);

    boolean existsByNameAndSubject(String name, Long subjectId);
}
