package com.example.questionbank.repository;

import com.example.questionbank.model.Question;
import com.example.questionbank.model.enums.SectionType;
import com.example.questionbank.model.enums.DifficultyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {

    // Existing methods
    Page<Question> findByQuestionTextContainingIgnoreCase(String questionText, Pageable pageable);
    List<Question> findByChapterSubjectId(Long subjectId);
    List<Question> findByChapterSubjectIdAndIsAddedToPaper(Long subjectId, boolean isAddedToPaper);

    // New methods
    List<Question> findByIsActiveTrue();
    Page<Question> findByIsActiveTrue(Pageable pageable);

    // Filter by chapter
    List<Question> findByChapterIdAndIsActiveTrue(Long chapterId);
    Page<Question> findByChapterIdAndIsActiveTrue(Long chapterId, Pageable pageable);

    // Filter by section type
    List<Question> findBySectionTypeAndIsActiveTrue(SectionType sectionType);
    Page<Question> findBySectionTypeAndIsActiveTrue(SectionType sectionType, Pageable pageable);

    // Filter by difficulty
    List<Question> findByDifficultyLevelAndIsActiveTrue(DifficultyLevel difficultyLevel);

    // Complex queries
    @Query("SELECT q FROM Question q WHERE " +
            "q.chapter.subject.id = :subjectId AND " +
            "q.isActive = true AND " +
            "(:isAddedToPaper IS NULL OR q.isAddedToPaper = :isAddedToPaper)")
    List<Question> findActiveQuestionsBySubjectAndPaperStatus(
            @Param("subjectId") Long subjectId,
            @Param("isAddedToPaper") Boolean isAddedToPaper);

    @Query("SELECT q FROM Question q WHERE " +
            "q.chapter.subject.classEntity.id = :classId AND " +
            "q.isActive = true")
    List<Question> findActiveQuestionsByClass(@Param("classId") Long classId);

    @Query("SELECT COUNT(q) FROM Question q WHERE " +
            "q.chapter.id = :chapterId AND " +
            "q.isActive = true")
    Long countActiveQuestionsByChapter(@Param("chapterId") Long chapterId);

    @Query("SELECT q FROM Question q WHERE " +
            "q.questionText LIKE %:searchText% AND " +
            "q.isActive = true")
    Page<Question> searchActiveQuestions(@Param("searchText") String searchText, Pageable pageable);
}