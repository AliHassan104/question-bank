package com.example.questionbank.repository;

import com.example.questionbank.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {
    Page<Question> findByQuestionTextContainingIgnoreCase(String questionText, Pageable pageable);

    List<Question> findByChapterSubjectId(Long subjectId);

    List<Question> findByChapterSubjectIdAndIsAddedToPaper(Long subjectId, boolean isAddedToPaper);
}


