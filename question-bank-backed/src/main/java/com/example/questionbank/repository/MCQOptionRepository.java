package com.example.questionbank.repository;

import com.example.questionbank.model.MCQOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MCQOptionRepository extends JpaRepository<MCQOption, Long> {

    // Existing methods
    Page<MCQOption> findByOptionTextContainingIgnoreCase(String optionText, Pageable pageable);
    List<MCQOption> findByQuestionId(Long questionId);
    List<MCQOption> findByQuestionIdIn(List<Long> questionIds);

    // New methods
    List<MCQOption> findByQuestionIdAndIsActiveTrue(Long questionId);
    List<MCQOption> findByQuestionIdInAndIsActiveTrue(List<Long> questionIds);

    // Find correct options
    List<MCQOption> findByQuestionIdAndIsCorrectTrue(Long questionId);
    List<MCQOption> findByQuestionIdAndIsCorrectTrueAndIsActiveTrue(Long questionId);

    // Ordered options
    List<MCQOption> findByQuestionIdOrderByOptionOrderAsc(Long questionId);
    List<MCQOption> findByQuestionIdAndIsActiveTrueOrderByOptionOrderAsc(Long questionId);

    // Count methods
    @Query("SELECT COUNT(o) FROM MCQOption o WHERE " +
            "o.question.id = :questionId AND " +
            "o.isActive = true")
    Long countActiveOptionsByQuestion(@Param("questionId") Long questionId);

    @Query("SELECT COUNT(o) FROM MCQOption o WHERE " +
            "o.question.id = :questionId AND " +
            "o.isCorrect = true AND " +
            "o.isActive = true")
    Long countCorrectOptionsByQuestion(@Param("questionId") Long questionId);

    // Validation queries
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
            "FROM MCQOption o WHERE " +
            "o.question.id = :questionId AND " +
            "o.isCorrect = true AND " +
            "o.isActive = true")
    boolean hasCorrectOption(@Param("questionId") Long questionId);

    // Delete by question
    void deleteByQuestionId(Long questionId);

    // Find options with images
    List<MCQOption> findByQuestionIdAndOptionImageUrlIsNotNull(Long questionId);
}