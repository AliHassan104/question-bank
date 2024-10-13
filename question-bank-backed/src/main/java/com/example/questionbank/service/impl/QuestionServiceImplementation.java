package com.example.questionbank.service.impl;

import com.example.questionbank.model.Question;
import com.example.questionbank.model.enums.SectionType;
import com.example.questionbank.repository.QuestionRepository;
import com.example.questionbank.repository.specification.QuestionSpecification;
import com.example.questionbank.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuestionServiceImplementation implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public Question updateQuestion(Long id, Question updatedQuestion) {
        Optional<Question> existingQuestionOpt = questionRepository.findById(id);
        if (existingQuestionOpt.isPresent()) {
            Question existingQuestion = existingQuestionOpt.get();
            existingQuestion.setQuestionText(updatedQuestion.getQuestionText());
            existingQuestion.setSectionType(updatedQuestion.getSectionType());
            existingQuestion.setChapter(updatedQuestion.getChapter());
            return questionRepository.save(existingQuestion);
        } else {
            throw new RuntimeException("Question not found with id " + id);
        }
    }

    @Override
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    @Override
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Question not found with id " + id));
    }

    @Override
    public Page<Question> getAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    @Override
    public Page<Question> searchQuestions(String questionText, Pageable pageable) {
        return questionRepository.findByQuestionTextContainingIgnoreCase(questionText, pageable);
    }

    @Override
    public Page<Question> getFilteredQuestions(SectionType sectionType, Long chapterId, Long subjectId, Long classId, Pageable pageable) {
        Specification<Question> spec = Specification
                .where(QuestionSpecification.filterBySectionType(sectionType))
                .and(QuestionSpecification.filterByChapter(chapterId))
                .and(QuestionSpecification.filterBySubject(subjectId))
                .and(QuestionSpecification.filterByClass(classId));

        return questionRepository.findAll(spec, pageable);
    }

    @Override
    public Question toggleAddedToPaper(Long id) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);

        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            question.setAddedToPaper(!question.isAddedToPaper()); // Toggle the value
            return questionRepository.save(question); // Save and return the updated question
        } else {
            throw new RuntimeException("Question not found with id: " + id);
        }
    }

    @Override
    public List<Question> getQuestionsBySubjectId(Long subjectId) {
        return questionRepository.findByChapterSubjectId(subjectId);
    }

    @Override
    public List<Question> getQuestionsBySubjectIdAndAddedToPaper(Long subjectId) {
        return questionRepository.findByChapterSubjectIdAndIsAddedToPaper(subjectId , true);
    }
}
