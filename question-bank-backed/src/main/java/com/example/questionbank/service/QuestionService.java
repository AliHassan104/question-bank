package com.example.questionbank.service;

import com.example.questionbank.model.Question;
import com.example.questionbank.model.enums.SectionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {
    Question createQuestion(Question question);
    Question updateQuestion(Long id, Question question);
    void deleteQuestion(Long id);
    Question getQuestionById(Long id);

    // Pagination and searching

    Page<Question> getAllQuestionsPagination(Pageable pageable);

    List<Question> getAllQuestions();
    Page<Question> searchQuestions(String questionText, Pageable pageable);
    Page<Question> getFilteredQuestions(SectionType sectionType, Long chapterId, Long subjectId, Long classId, Pageable pageable);

    Question toggleAddedToPaper(Long id);

    List<Question> getQuestionsBySubjectId(Long subjectId);
    List<Question> getQuestionsBySubjectIdAndAddedToPaper(Long subjectId);

}

