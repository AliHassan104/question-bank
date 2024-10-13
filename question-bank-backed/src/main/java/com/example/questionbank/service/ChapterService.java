package com.example.questionbank.service;

import com.example.questionbank.model.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChapterService {
    // CRUD operations
    Chapter createChapter(Chapter chapter);
    Chapter updateChapter(Long id, Chapter chapter);
    void deleteChapter(Long id);
    Chapter getChapterById(Long id);

    // Pagination, sorting, and searching
    Page<Chapter> getAllChaptersWithPagination(Pageable pageable);
    Page<Chapter> searchChapters(String name, Pageable pageable);

    List<Chapter> filterChapters(Long subjectId, Long classId) ;

    List<Chapter> getAllChapters();
}
