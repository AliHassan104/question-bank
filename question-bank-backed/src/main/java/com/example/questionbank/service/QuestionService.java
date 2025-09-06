package com.example.questionbank.service;

import com.example.questionbank.dto.request.CreateQuestionRequestDTO;
import com.example.questionbank.dto.request.UpdateQuestionRequestDTO;
import com.example.questionbank.dto.response.QuestionResponseDTO;
import com.example.questionbank.model.enums.SectionType;
import com.example.questionbank.model.enums.QuestionType;
import com.example.questionbank.model.enums.DifficultyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface QuestionService {

    // Basic CRUD operations
    QuestionResponseDTO createQuestion(CreateQuestionRequestDTO dto);
    QuestionResponseDTO updateQuestion(Long id, UpdateQuestionRequestDTO dto);
    void deleteQuestion(Long id);
    QuestionResponseDTO getQuestionById(Long id);
    QuestionResponseDTO getQuestionByIdWithOptions(Long id);

    // Bulk operations
    List<QuestionResponseDTO> createQuestions(List<CreateQuestionRequestDTO> dtos);
    List<QuestionResponseDTO> updateQuestions(List<UpdateQuestionRequestDTO> dtos);
    void deleteQuestionsByIds(List<Long> ids);

    // Basic listing and pagination
    List<QuestionResponseDTO> getAllQuestions();
    List<QuestionResponseDTO> getAllActiveQuestions();
    Page<QuestionResponseDTO> getAllQuestionsPagination(Pageable pageable);
    Page<QuestionResponseDTO> getAllActiveQuestionsPagination(Pageable pageable);

    // Search operations
    Page<QuestionResponseDTO> searchQuestions(String questionText, Pageable pageable);
    Page<QuestionResponseDTO> searchActiveQuestions(String searchText, Pageable pageable);
    List<QuestionResponseDTO> searchQuestionsByKeywords(List<String> keywords);

    // Filtering operations
    Page<QuestionResponseDTO> getFilteredQuestions(
            SectionType sectionType,
            QuestionType questionType,
            DifficultyLevel difficultyLevel,
            Long chapterId,
            Long subjectId,
            Long classId,
            Boolean isAddedToPaper,
            Double minMarks,
            Double maxMarks,
            Pageable pageable
    );

    List<QuestionResponseDTO> getQuestionsByFilter(
            SectionType sectionType,
            QuestionType questionType,
            DifficultyLevel difficultyLevel,
            Long chapterId,
            Long subjectId,
            Long classId,
            Boolean isAddedToPaper
    );

    // Chapter-based operations
    List<QuestionResponseDTO> getQuestionsByChapter(Long chapterId);
    List<QuestionResponseDTO> getActiveQuestionsByChapter(Long chapterId);
    Page<QuestionResponseDTO> getQuestionsByChapter(Long chapterId, Pageable pageable);
    Long countQuestionsByChapter(Long chapterId);

    // Subject-based operations
    List<QuestionResponseDTO> getQuestionsBySubject(Long subjectId);
    List<QuestionResponseDTO> getActiveQuestionsBySubject(Long subjectId);
    List<QuestionResponseDTO> getQuestionsBySubjectAndAddedToPaper(Long subjectId);
    List<QuestionResponseDTO> getQuestionsBySubjectAndNotAddedToPaper(Long subjectId);
    Map<SectionType, List<QuestionResponseDTO>> getQuestionsBySubjectGroupedBySection(Long subjectId);
    Long countQuestionsBySubject(Long subjectId);

    // Class-based operations
    List<QuestionResponseDTO> getQuestionsByClass(Long classId);
    List<QuestionResponseDTO> getActiveQuestionsByClass(Long classId);
    Long countQuestionsByClass(Long classId);

    // Section type operations
    List<QuestionResponseDTO> getQuestionsBySectionType(SectionType sectionType);
    List<QuestionResponseDTO> getMCQQuestions();
    List<QuestionResponseDTO> getShortAnswerQuestions();
    List<QuestionResponseDTO> getLongAnswerQuestions();
    Page<QuestionResponseDTO> getQuestionsBySectionType(SectionType sectionType, Pageable pageable);

    // Difficulty-based operations
    List<QuestionResponseDTO> getQuestionsByDifficulty(DifficultyLevel difficultyLevel);
    List<QuestionResponseDTO> getQuestionsByDifficultyRange(DifficultyLevel minDifficulty, DifficultyLevel maxDifficulty);
    Map<DifficultyLevel, Long> getQuestionCountByDifficulty(Long subjectId);

    // Paper management operations
    QuestionResponseDTO toggleAddedToPaper(Long id);
    void addQuestionToPaper(Long id);
    void removeQuestionFromPaper(Long id);
    void addQuestionsToPaper(List<Long> questionIds);
    void removeQuestionsFromPaper(List<Long> questionIds);
    List<QuestionResponseDTO> getQuestionsAddedToPaper();
    List<QuestionResponseDTO> getQuestionsNotAddedToPaper();
    Page<QuestionResponseDTO> getQuestionsAddedToPaper(Pageable pageable);

    // Marks-based operations
    List<QuestionResponseDTO> getQuestionsByMarksRange(Double minMarks, Double maxMarks);
    Double getTotalMarksBySubject(Long subjectId);
    Double getTotalMarksByChapter(Long chapterId);
    Map<SectionType, Double> getTotalMarksBySubjectAndSection(Long subjectId);

    // Validation and business logic
    boolean validateQuestionForPaper(Long questionId);
    List<String> getValidationErrorsForQuestion(Long questionId);
    boolean hasValidMCQOptions(Long questionId);
    void validateQuestionBeforeCreate(CreateQuestionRequestDTO dto);
    void validateQuestionBeforeUpdate(Long id, UpdateQuestionRequestDTO dto);

    // Activation/Deactivation
    void activateQuestion(Long id);
    void deactivateQuestion(Long id);
    void activateQuestions(List<Long> ids);
    void deactivateQuestions(List<Long> ids);

    // Question duplication and cloning
    QuestionResponseDTO duplicateQuestion(Long questionId);
    QuestionResponseDTO duplicateQuestionToChapter(Long questionId, Long targetChapterId);
    List<QuestionResponseDTO> duplicateQuestionsToChapter(List<Long> questionIds, Long targetChapterId);

    // Statistics and reporting
    Map<String, Long> getQuestionStatsBySubject(Long subjectId);
    Map<String, Long> getQuestionStatsByChapter(Long chapterId);
    Map<String, Long> getQuestionStatsByClass(Long classId);
    Map<SectionType, Long> getQuestionCountBySectionType(Long subjectId);
    Map<QuestionType, Long> getQuestionCountByQuestionType(Long subjectId);

    // Import/Export operations
    List<QuestionResponseDTO> importQuestionsFromTemplate(List<Map<String, Object>> questionsData);
    List<Map<String, Object>> exportQuestionsToTemplate(List<Long> questionIds);
    List<Map<String, Object>> exportQuestionsBySubject(Long subjectId);

    // Random question selection
    List<QuestionResponseDTO> getRandomQuestions(int count);
    List<QuestionResponseDTO> getRandomQuestionsBySubject(Long subjectId, int count);
    List<QuestionResponseDTO> getRandomQuestionsByChapter(Long chapterId, int count);
    List<QuestionResponseDTO> getRandomQuestionsByCriteria(
            SectionType sectionType,
            DifficultyLevel difficultyLevel,
            Long subjectId,
            int count
    );

    // Question ordering and arrangement
    void updateQuestionOrder(Long questionId, Integer newOrder);
    void reorderQuestionsInChapter(Long chapterId, Map<Long, Integer> questionOrderMap);
    List<QuestionResponseDTO> getQuestionsInOrder(Long chapterId);

    // Advanced search and filtering
    Page<QuestionResponseDTO> advancedSearch(
            String searchText,
            List<SectionType> sectionTypes,
            List<QuestionType> questionTypes,
            List<DifficultyLevel> difficultyLevels,
            List<Long> chapterIds,
            List<Long> subjectIds,
            List<Long> classIds,
            Boolean isAddedToPaper,
            Boolean isActive,
            Double minMarks,
            Double maxMarks,
            Pageable pageable
    );

    // Question recommendation
    List<QuestionResponseDTO> getRecommendedQuestions(Long questionId, int count);
    List<QuestionResponseDTO> getSimilarQuestions(Long questionId, int count);
    List<QuestionResponseDTO> getQuestionsByDifficultyProgression(Long subjectId, DifficultyLevel startLevel, int count);
}