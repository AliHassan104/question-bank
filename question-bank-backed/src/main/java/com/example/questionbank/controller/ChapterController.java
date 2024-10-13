package com.example.questionbank.controller;

import com.example.questionbank.model.Chapter;
import com.example.questionbank.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @PostMapping
    public ResponseEntity<Chapter> createChapter(@RequestBody Chapter chapter) {
        Chapter createdChapter = chapterService.createChapter(chapter);
        return new ResponseEntity<>(createdChapter, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chapter> updateChapter(@PathVariable Long id, @RequestBody Chapter chapter) {
        Chapter updatedChapter = chapterService.updateChapter(id, chapter);
        return ResponseEntity.ok(updatedChapter);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chapter> getChapterById(@PathVariable Long id) {
        Chapter chapter = chapterService.getChapterById(id);
        return ResponseEntity.ok(chapter);
    }

    @GetMapping
    public ResponseEntity<List<Chapter>> getAllChapters() {
        List<Chapter> chapters = chapterService.getAllChapters();
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Chapter>> getAllChaptersWithPagination(Pageable pageable) {
        Page<Chapter> chapters = chapterService.getAllChaptersWithPagination(pageable);
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Chapter>> searchChapters(@RequestParam String name, Pageable pageable) {
        Page<Chapter> chapters = chapterService.searchChapters(name, pageable);
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Chapter>> filterChapters(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long classId
            ) {
        List<Chapter> chapters = chapterService.filterChapters(subjectId, classId);
        return ResponseEntity.ok(chapters);
    }
}
