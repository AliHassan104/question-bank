package com.example.questionbank.service;

import com.example.questionbank.model.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChapterService {
    // CRUD operations
    Chapter createChapter(Chapter chapter);
    Chapter updateChapter(Long id, Chapter chapter);
    void deleteChapter(Long id);
    Chapter getChapterById(Long id);

    // Pagination, sorting, and searching
    Page<Chapter> getAllChapters(Pageable pageable);
    Page<Chapter> searchChapters(String name, Pageable pageable);
}
