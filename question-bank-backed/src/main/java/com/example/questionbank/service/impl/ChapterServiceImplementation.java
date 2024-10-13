package com.example.questionbank.service.impl;

import com.example.questionbank.model.Chapter;
import com.example.questionbank.repository.ChapterRepository;
import com.example.questionbank.service.ChapterService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChapterServiceImplementation implements ChapterService {

    @Autowired
    private ChapterRepository chapterRepository;

    @Override
    public Chapter createChapter(Chapter chapter) {
        return chapterRepository.save(chapter);
    }

    @Override
    public Chapter updateChapter(Long id, Chapter updatedChapter) {
        Optional<Chapter> existingChapterOpt = chapterRepository.findById(id);
        if (existingChapterOpt.isPresent()) {
            Chapter existingChapter = existingChapterOpt.get();
            existingChapter.setName(updatedChapter.getName());
            existingChapter.setSubject(updatedChapter.getSubject());
            return chapterRepository.save(existingChapter);
        } else {
            throw new RuntimeException("Chapter not found with id " + id);
        }
    }

    @Override
    public void deleteChapter(Long id) {
        chapterRepository.deleteById(id);
    }

    @Override
    public Chapter getChapterById(Long id) {
        return chapterRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Chapter not found with id " + id));
    }

    @Override
    public Page<Chapter> getAllChaptersWithPagination(Pageable pageable) {
        return chapterRepository.findAll(pageable);
    }

    @Override
    public Page<Chapter> searchChapters(String name, Pageable pageable) {
        return chapterRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public List<Chapter> filterChapters(Long subjectId, Long classId) {
        return chapterRepository.findBySubjectAndClass(subjectId, classId);
    }

    @Override
    public List<Chapter> getAllChapters() {
        return chapterRepository.findAll();
    }
}

