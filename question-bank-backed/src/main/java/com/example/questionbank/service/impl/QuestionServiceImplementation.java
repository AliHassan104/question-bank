package com.example.questionbank.service.impl;

import com.example.questionbank.dto.request.CreateQuestionRequestDTO;
import com.example.questionbank.dto.request.UpdateQuestionRequestDTO;
import com.example.questionbank.dto.response.QuestionResponseDTO;
import com.example.questionbank.exception.RecordNotFoundException;
import com.example.questionbank.exception.ValidationException;
import com.example.questionbank.mapper.QuestionMapper;
import com.example.questionbank.model.Chapter;
import com.example.questionbank.model.Question;
import com.example.questionbank.model.enums.SectionType;
import com.example.questionbank.model.enums.QuestionType;
import com.example.questionbank.model.enums.DifficultyLevel;
import com.example.questionbank.repository.ChapterRepository;
import com.example.questionbank.repository.QuestionRepository;
import com.example.questionbank.repository.specification.QuestionSpecification;
import com.example.questionbank.service.QuestionService;
import com.example.questionbank.service.MCQOptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class QuestionServiceImplementation implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private MCQOptionService mcqOptionService;

    @Override
    public QuestionResponseDTO createQuestion(CreateQuestionRequestDTO dto) {
        log.info("Creating question: {}", dto.getQuestionText().substring(0, Math.min(50, dto.getQuestionText().length())));

        validateQuestionBeforeCreate(dto);

        // Validate chapter exists
        Chapter chapter = chapterRepository.findById(dto.getChapterId())
                .orElseThrow(() -> new RecordNotFoundException("Chapter", "id", dto.getChapterId()));

        try {
            Question question = questionMapper.toEntity(dto);
            question.setChapter(chapter);
            Question savedQuestion = questionRepository.save(question);

            // Create MCQ options if provided
            if (dto.getMcqOptions() != null && !dto.getMcqOptions().isEmpty()) {
                mcqOptionService.createOptionsForQuestion(savedQuestion.getId(), dto.getMcqOptions());
            }

            log.info("Successfully created question with ID: {}", savedQuestion.getId());
            return getQuestionByIdWithOptions(savedQuestion.getId());

        } catch (Exception e) {
            log.error("Failed to create question", e);
            throw new RuntimeException("Failed to create question", e);
        }
    }

    @Override
    public QuestionResponseDTO updateQuestion(Long id, UpdateQuestionRequestDTO dto) {
        log.info("Updating question with ID: {}", id);

        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", id));

        validateQuestionBeforeUpdate(id, dto);

        // Validate chapter exists if changed
        if (!existingQuestion.getChapter().getId().equals(dto.getChapterId())) {
            Chapter newChapter = chapterRepository.findById(dto.getChapterId())
                    .orElseThrow(() -> new RecordNotFoundException("Chapter", "id", dto.getChapterId()));
            existingQuestion.setChapter(newChapter);
        }

        try {
            questionMapper.updateEntityFromDTO(dto, existingQuestion);
            Question updatedQuestion = questionRepository.save(existingQuestion);

            // Update MCQ options if provided
            if (dto.getMcqOptions() != null) {
                mcqOptionService.updateOptionsForQuestion(updatedQuestion.getId(), dto.getMcqOptions());
            }

            log.info("Successfully updated question with ID: {}", id);
            return getQuestionByIdWithOptions(updatedQuestion.getId());

        } catch (Exception e) {
            log.error("Failed to update question with ID: {}", id, e);
            throw new RuntimeException("Failed to update question", e);
        }
    }

    @Override
    public void deleteQuestion(Long id) {
        log.info("Deleting question with ID: {}", id);

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", id));

        try {
            // Soft delete
            question.setIsActive(false);
            questionRepository.save(question);

            // Deactivate associated MCQ options
            mcqOptionService.deactivateAllOptionsForQuestion(id);

            log.info("Successfully soft deleted question with ID: {}", id);

        } catch (Exception e) {
            log.error("Failed to delete question with ID: {}", id, e);
            throw new RuntimeException("Failed to delete question", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDTO getQuestionById(Long id) {
        log.debug("Fetching question with ID: {}", id);

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", id));

        return questionMapper.toResponseDTOWithoutOptions(question);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDTO getQuestionByIdWithOptions(Long id) {
        log.debug("Fetching question with ID and options: {}", id);

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", id));

        QuestionResponseDTO responseDTO = questionMapper.toResponseDTO(question);

        // Load MCQ options separately for better performance
        if (question.isMCQType()) {
            responseDTO.setMcqOptions(mcqOptionService.getActiveOptionsByQuestionId(id));
        }

        return responseDTO;
    }

    @Override
    public List<QuestionResponseDTO> createQuestions(List<CreateQuestionRequestDTO> dtos) {
        log.info("Creating {} questions", dtos.size());

        List<QuestionResponseDTO> createdQuestions = new ArrayList<>();

        try {
            for (CreateQuestionRequestDTO dto : dtos) {
                createdQuestions.add(createQuestion(dto));
            }

            log.info("Successfully created {} questions", createdQuestions.size());
            return createdQuestions;

        } catch (Exception e) {
            log.error("Failed to create questions", e);
            throw new RuntimeException("Failed to create questions", e);
        }
    }

    @Override
    public List<QuestionResponseDTO> updateQuestions(List<UpdateQuestionRequestDTO> dtos) {
        log.info("Updating {} questions", dtos.size());

        List<QuestionResponseDTO> updatedQuestions = new ArrayList<>();

        try {
            for (UpdateQuestionRequestDTO dto : dtos) {
                if (dto.getId() != null) {
                    updatedQuestions.add(updateQuestion(dto.getId(), dto));
                }
            }

            log.info("Successfully updated {} questions", updatedQuestions.size());
            return updatedQuestions;

        } catch (Exception e) {
            log.error("Failed to update questions", e);
            throw new RuntimeException("Failed to update questions", e);
        }
    }

    @Override
    public void deleteQuestionsByIds(List<Long> ids) {
        log.info("Deleting {} questions", ids.size());

        try {
            for (Long id : ids) {
                deleteQuestion(id);
            }

            log.info("Successfully deleted {} questions", ids.size());

        } catch (Exception e) {
            log.error("Failed to delete questions", e);
            throw new RuntimeException("Failed to delete questions", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getAllQuestions() {
        log.debug("Fetching all questions");

        List<Question> questions = questionRepository.findAll();
        return questionMapper.toResponseDTOListWithoutOptions(questions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getAllActiveQuestions() {
        log.debug("Fetching all active questions");

        List<Question> questions = questionRepository.findByIsActiveTrue();
        return questionMapper.toResponseDTOListWithoutOptions(questions);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDTO> getAllQuestionsPagination(Pageable pageable) {
        log.debug("Fetching questions with pagination");

        Page<Question> questions = questionRepository.findAll(pageable);
        return questions.map(questionMapper::toResponseDTOWithoutOptions);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDTO> getAllActiveQuestionsPagination(Pageable pageable) {
        log.debug("Fetching active questions with pagination");

        Page<Question> questions = questionRepository.findByIsActiveTrue(pageable);
        return questions.map(questionMapper::toResponseDTOWithoutOptions);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDTO> searchQuestions(String questionText, Pageable pageable) {
        log.debug("Searching questions with text: {}", questionText);

        Page<Question> questions = questionRepository.findByQuestionTextContainingIgnoreCase(questionText, pageable);
        return questions.map(questionMapper::toResponseDTOWithoutOptions);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDTO> searchActiveQuestions(String searchText, Pageable pageable) {
        log.debug("Searching active questions with text: {}", searchText);

        Page<Question> questions = questionRepository.searchActiveQuestions(searchText, pageable);
        return questions.map(questionMapper::toResponseDTOWithoutOptions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> searchQuestionsByKeywords(List<String> keywords) {
        log.debug("Searching questions by keywords: {}", keywords);

        Specification<Question> spec = QuestionSpecification.isActive();

        for (String keyword : keywords) {
            spec = spec.and(QuestionSpecification.searchByText(keyword));
        }

        List<Question> questions = questionRepository.findAll(spec);
        return questionMapper.toResponseDTOListWithoutOptions(questions);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDTO> getFilteredQuestions(
            SectionType sectionType,
            QuestionType questionType,
            DifficultyLevel difficultyLevel,
            Long chapterId,
            Long subjectId,
            Long classId,
            Boolean isAddedToPaper,
            Double minMarks,
            Double maxMarks,
            Pageable pageable) {

        log.debug("Filtering questions with multiple criteria");

        Specification<Question> spec = Specification
                .where(QuestionSpecification.isActive())
                .and(QuestionSpecification.filterBySectionType(sectionType))
                .and(QuestionSpecification.filterByQuestionType(questionType))
                .and(QuestionSpecification.filterByDifficulty(difficultyLevel))
                .and(QuestionSpecification.filterByChapter(chapterId))
                .and(QuestionSpecification.filterBySubject(subjectId))
                .and(QuestionSpecification.filterByClass(classId))
                .and(QuestionSpecification.filterByPaperStatus(isAddedToPaper))
                .and(QuestionSpecification.filterByMarksRange(minMarks, maxMarks));

        Page<Question> questions = questionRepository.findAll(spec, pageable);
        return questions.map(questionMapper::toResponseDTOWithoutOptions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsByFilter(
            SectionType sectionType,
            QuestionType questionType,
            DifficultyLevel difficultyLevel,
            Long chapterId,
            Long subjectId,
            Long classId,
            Boolean isAddedToPaper) {

        return getFilteredQuestions(sectionType, questionType, difficultyLevel,
                chapterId, subjectId, classId, isAddedToPaper, null, null,
                Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsByChapter(Long chapterId) {
        log.debug("Fetching questions for chapter ID: {}", chapterId);

        List<Question> questions = questionRepository.findByChapterIdAndIsActiveTrue(chapterId);
        return questionMapper.toResponseDTOListWithoutOptions(questions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getActiveQuestionsByChapter(Long chapterId) {
        return getQuestionsByChapter(chapterId); // Already filters for active
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDTO> getQuestionsByChapter(Long chapterId, Pageable pageable) {
        log.debug("Fetching questions for chapter ID: {} with pagination", chapterId);

        Page<Question> questions = questionRepository.findByChapterIdAndIsActiveTrue(chapterId, pageable);
        return questions.map(questionMapper::toResponseDTOWithoutOptions);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countQuestionsByChapter(Long chapterId) {
        return questionRepository.countActiveQuestionsByChapter(chapterId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsBySubject(Long subjectId) {
        log.debug("Fetching questions for subject ID: {}", subjectId);

        List<Question> questions = questionRepository.findByChapterSubjectId(subjectId);
        return questionMapper.toResponseDTOListWithoutOptions(questions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getActiveQuestionsBySubject(Long subjectId) {
        log.debug("Fetching active questions for subject ID: {}", subjectId);

        List<Question> questions = questionRepository.findActiveQuestionsBySubjectAndPaperStatus(subjectId, null);

        return questionMapper.toResponseDTOListWithoutOptions(questions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsBySubjectAndAddedToPaper(Long subjectId) {
        log.debug("Fetching questions added to paper for subject ID: {}", subjectId);

        List<Question> questions = questionRepository.findByChapterSubjectIdAndIsAddedToPaper(subjectId, true);
        return questionMapper.toResponseDTOListWithoutOptions(questions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsBySubjectAndNotAddedToPaper(Long subjectId) {
        log.debug("Fetching questions not added to paper for subject ID: {}", subjectId);

        List<Question> questions = questionRepository.findActiveQuestionsBySubjectAndPaperStatus(subjectId, false);
        return questionMapper.toResponseDTOListWithoutOptions(questions);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<SectionType, List<QuestionResponseDTO>> getQuestionsBySubjectGroupedBySection(Long subjectId) {
        log.debug("Fetching questions grouped by section for subject ID: {}", subjectId);

        List<Question> questions = questionRepository.findActiveQuestionsBySubjectAndPaperStatus(subjectId, null);

        return questions.stream()
                .collect(Collectors.groupingBy(
                        Question::getSectionType,
                        Collectors.mapping(
                                questionMapper::toResponseDTOWithoutOptions,
                                Collectors.toList()
                        )
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Long countQuestionsBySubject(Long subjectId) {
        List<Question> questions = questionRepository.findActiveQuestionsBySubjectAndPaperStatus(subjectId, null);
        return (long) questions.size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsByClass(Long classId) {
        log.debug("Fetching questions for class ID: {}", classId);

        List<Question> questions = questionRepository.findActiveQuestionsByClass(classId);
        return questionMapper.toResponseDTOListWithoutOptions(questions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getActiveQuestionsByClass(Long classId) {
        return getQuestionsByClass(classId); // Already filters for active
    }

    @Override
    @Transactional(readOnly = true)
    public Long countQuestionsByClass(Long classId) {
        List<Question> questions = questionRepository.findActiveQuestionsByClass(classId);
        return (long) questions.size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsBySectionType(SectionType sectionType) {
        log.debug("Fetching questions by section type: {}", sectionType);

        List<Question> questions = questionRepository.findBySectionTypeAndIsActiveTrue(sectionType);
        return questionMapper.toResponseDTOListWithoutOptions(questions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getMCQQuestions() {
        return getQuestionsBySectionType(SectionType.MCQ);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getShortAnswerQuestions() {
        return getQuestionsBySectionType(SectionType.SHORT_QUESTION);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getLongAnswerQuestions() {
        return getQuestionsBySectionType(SectionType.LONG_QUESTION);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDTO> getQuestionsBySectionType(SectionType sectionType, Pageable pageable) {
        log.debug("Fetching questions by section type: {} with pagination", sectionType);

        Page<Question> questions = questionRepository.findBySectionTypeAndIsActiveTrue(sectionType, pageable);
        return questions.map(questionMapper::toResponseDTOWithoutOptions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsByDifficulty(DifficultyLevel difficultyLevel) {
        log.debug("Fetching questions by difficulty: {}", difficultyLevel);

        List<Question> questions = questionRepository.findByDifficultyLevelAndIsActiveTrue(difficultyLevel);
        return questionMapper.toResponseDTOListWithoutOptions(questions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsByDifficultyRange(DifficultyLevel minDifficulty, DifficultyLevel maxDifficulty) {
        log.debug("Fetching questions by difficulty range: {} to {}", minDifficulty, maxDifficulty);

        Specification<Question> spec = QuestionSpecification.isActive();

        if (minDifficulty != null && maxDifficulty != null) {
            // Custom logic for difficulty range filtering
            List<DifficultyLevel> validLevels = Arrays.stream(DifficultyLevel.values())
                    .filter(level -> level.getLevel() >= minDifficulty.getLevel() &&
                            level.getLevel() <= maxDifficulty.getLevel())
                    .collect(Collectors.toList());

            Specification<Question> difficultySpec = (root, query, criteriaBuilder) ->
                    root.get("difficultyLevel").in(validLevels);

            spec = spec.and(difficultySpec);
        }

        List<Question> questions = questionRepository.findAll(spec);
        return questionMapper.toResponseDTOListWithoutOptions(questions);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<DifficultyLevel, Long> getQuestionCountByDifficulty(Long subjectId) {
        log.debug("Getting question count by difficulty for subject ID: {}", subjectId);

        List<Question> questions = questionRepository.findActiveQuestionsBySubjectAndPaperStatus(subjectId, null);

        return questions.stream()
                .collect(Collectors.groupingBy(
                        Question::getDifficultyLevel,
                        Collectors.counting()
                ));
    }

    @Override
    public QuestionResponseDTO toggleAddedToPaper(Long id) {
        log.info("Toggling paper status for question ID: {}", id);

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", id));

        question.togglePaperStatus();
        Question updatedQuestion = questionRepository.save(question);

        log.info("Successfully toggled paper status for question ID: {}", id);
        return questionMapper.toResponseDTOWithoutOptions(updatedQuestion);
    }

    @Override
    public void addQuestionToPaper(Long id) {
        log.info("Adding question to paper: {}", id);

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", id));

        if (!validateQuestionForPaper(id)) {
            throw new ValidationException("Question is not valid for paper",
                    Map.of("validation", String.join(", ", getValidationErrorsForQuestion(id))));
        }

        question.setIsAddedToPaper(true);
        questionRepository.save(question);
    }

    @Override
    public void removeQuestionFromPaper(Long id) {
        log.info("Removing question from paper: {}", id);

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", id));

        question.setIsAddedToPaper(false);
        questionRepository.save(question);
    }

    @Override
    public void addQuestionsToPaper(List<Long> questionIds) {
        log.info("Adding {} questions to paper", questionIds.size());

        for (Long id : questionIds) {
            addQuestionToPaper(id);
        }
    }

    @Override
    public void removeQuestionsFromPaper(List<Long> questionIds) {
        log.info("Removing {} questions from paper", questionIds.size());

        for (Long id : questionIds) {
            removeQuestionFromPaper(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsAddedToPaper() {
        log.debug("Fetching questions added to paper");

        Specification<Question> spec = QuestionSpecification.isActive()
                .and(QuestionSpecification.filterByPaperStatus(true));

        List<Question> questions = questionRepository.findAll(spec);
        return questionMapper.toResponseDTOListWithoutOptions(questions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsNotAddedToPaper() {
        log.debug("Fetching questions not added to paper");

        Specification<Question> spec = QuestionSpecification.isActive()
                .and(QuestionSpecification.filterByPaperStatus(false));

        List<Question> questions = questionRepository.findAll(spec);
        return questionMapper.toResponseDTOListWithoutOptions(questions);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDTO> getQuestionsAddedToPaper(Pageable pageable) {
        log.debug("Fetching questions added to paper with pagination");

        Specification<Question> spec = QuestionSpecification.isActive()
                .and(QuestionSpecification.filterByPaperStatus(true));

        Page<Question> questions = questionRepository.findAll(spec, pageable);
        return questions.map(questionMapper::toResponseDTOWithoutOptions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsByMarksRange(Double minMarks, Double maxMarks) {
        log.debug("Fetching questions by marks range: {} to {}", minMarks, maxMarks);

        Specification<Question> spec = QuestionSpecification.isActive()
                .and(QuestionSpecification.filterByMarksRange(minMarks, maxMarks));

        List<Question> questions = questionRepository.findAll(spec);
        return questionMapper.toResponseDTOListWithoutOptions(questions);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalMarksBySubject(Long subjectId) {
        log.debug("Calculating total marks for subject ID: {}", subjectId);

        List<Question> questions = questionRepository.findActiveQuestionsBySubjectAndPaperStatus(subjectId, true);
        return questions.stream()
                .mapToDouble(Question::getMarks)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalMarksByChapter(Long chapterId) {
        log.debug("Calculating total marks for chapter ID: {}", chapterId);

        List<Question> questions = questionRepository.findByChapterIdAndIsActiveTrue(chapterId);
        return questions.stream()
                .filter(Question::isAddedToPaper)
                .mapToDouble(Question::getMarks)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<SectionType, Double> getTotalMarksBySubjectAndSection(Long subjectId) {
        log.debug("Calculating total marks by section for subject ID: {}", subjectId);

        List<Question> questions = questionRepository.findActiveQuestionsBySubjectAndPaperStatus(subjectId, true);

        return questions.stream()
                .collect(Collectors.groupingBy(
                        Question::getSectionType,
                        Collectors.summingDouble(Question::getMarks)
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateQuestionForPaper(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", questionId));

        return question.isValidForPaper() &&
                (!question.isMCQType() || hasValidMCQOptions(questionId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getValidationErrorsForQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", questionId));

        List<String> errors = new ArrayList<>();

        if (!question.getIsActive()) {
            errors.add("Question is not active");
        }

        if (question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
            errors.add("Question text is required");
        }

        if (question.isMCQType() && !hasValidMCQOptions(questionId)) {
            errors.add("MCQ questions must have valid options with at least one correct answer");
        }

        if (question.getMarks() == null || question.getMarks() <= 0) {
            errors.add("Question must have valid marks");
        }

        return errors;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasValidMCQOptions(Long questionId) {
        if (!mcqOptionService.hasCorrectOption(questionId)) {
            return false;
        }

        Long optionCount = mcqOptionService.countOptionsByQuestion(questionId);
        return optionCount >= 2;
    }

    @Override
    public void validateQuestionBeforeCreate(CreateQuestionRequestDTO dto) {
        Map<String, String> errors = new HashMap<>();

        // Validate chapter exists
        if (!chapterRepository.existsById(dto.getChapterId())) {
            errors.put("chapterId", "Chapter not found");
        }

        // Validate MCQ options
        if (dto.getSectionType() == SectionType.MCQ) {
            if (dto.getMcqOptions() == null || dto.getMcqOptions().size() < 2) {
                errors.put("mcqOptions", "MCQ questions must have at least 2 options");
            } else {
                long correctCount = dto.getMcqOptions().stream()
                        .mapToLong(opt -> Boolean.TRUE.equals(opt.getIsCorrect()) ? 1 : 0)
                        .sum();

                if (dto.getQuestionType() == QuestionType.SINGLE_CHOICE && correctCount != 1) {
                    errors.put("correctOptions", "Single choice questions must have exactly 1 correct option");
                } else if (dto.getQuestionType() == QuestionType.MULTIPLE_CHOICE && correctCount < 1) {
                    errors.put("correctOptions", "Multiple choice questions must have at least 1 correct option");
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Question validation failed", errors);
        }
    }

    @Override
    public void validateQuestionBeforeUpdate(Long id, UpdateQuestionRequestDTO dto) {
        Map<String, String> errors = new HashMap<>();

        // Validate chapter exists
        if (!chapterRepository.existsById(dto.getChapterId())) {
            errors.put("chapterId", "Chapter not found");
        }

        // Validate MCQ options for MCQ questions
        if (dto.getSectionType() == SectionType.MCQ && dto.getMcqOptions() != null) {
            if (dto.getMcqOptions().size() < 2) {
                errors.put("mcqOptions", "MCQ questions must have at least 2 options");
            } else {
                long correctCount = dto.getMcqOptions().stream()
                        .mapToLong(opt -> Boolean.TRUE.equals(opt.getIsCorrect()) ? 1 : 0)
                        .sum();

                if (dto.getQuestionType() == QuestionType.SINGLE_CHOICE && correctCount != 1) {
                    errors.put("correctOptions", "Single choice questions must have exactly 1 correct option");
                } else if (dto.getQuestionType() == QuestionType.MULTIPLE_CHOICE && correctCount < 1) {
                    errors.put("correctOptions", "Multiple choice questions must have at least 1 correct option");
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Question validation failed", errors);
        }
    }

    @Override
    public void activateQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", id));
        question.setIsActive(true);
        questionRepository.save(question);
    }

    @Override
    public void deactivateQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", id));
        question.setIsActive(false);
        questionRepository.save(question);
    }

    @Override
    public void activateQuestions(List<Long> ids) {
        for (Long id : ids) {
            activateQuestion(id);
        }
    }

    @Override
    public void deactivateQuestions(List<Long> ids) {
        for (Long id : ids) {
            deactivateQuestion(id);
        }
    }

    @Override
    public QuestionResponseDTO duplicateQuestion(Long questionId) {
        log.info("Duplicating question ID: {}", questionId);

        Question originalQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", questionId));

        try {
            Question duplicatedQuestion = Question.builder()
                    .questionText(originalQuestion.getQuestionText() + " (Copy)")
                    .questionImageUrl(originalQuestion.getQuestionImageUrl())
                    .explanation(originalQuestion.getExplanation())
                    .sectionType(originalQuestion.getSectionType())
                    .questionType(originalQuestion.getQuestionType())
                    .difficultyLevel(originalQuestion.getDifficultyLevel())
                    .marks(originalQuestion.getMarks())
                    .negativeMarks(originalQuestion.getNegativeMarks())
                    .timeLimitSeconds(originalQuestion.getTimeLimitSeconds())
                    .chapter(originalQuestion.getChapter())
                    .isAddedToPaper(false)
                    .isActive(true)
                    .build();

            Question savedQuestion = questionRepository.save(duplicatedQuestion);

            // Duplicate MCQ options if it's an MCQ question
            if (originalQuestion.isMCQType()) {
                List<com.example.questionbank.dto.response.MCQOptionResponseDTO> originalOptions =
                        mcqOptionService.getActiveOptionsByQuestionId(questionId);

                List<com.example.questionbank.dto.request.CreateMCQOptionRequestDTO> newOptions =
                        originalOptions.stream()
                                .map(opt -> com.example.questionbank.dto.request.CreateMCQOptionRequestDTO.builder()
                                        .optionText(opt.getOptionText())
                                        .isCorrect(opt.getIsCorrect())
                                        .optionOrder(opt.getOptionOrder())
                                        .optionImageUrl(opt.getOptionImageUrl())
                                        .build())
                                .collect(Collectors.toList());

                mcqOptionService.createOptionsForQuestion(savedQuestion.getId(), newOptions);
            }

            log.info("Successfully duplicated question. New ID: {}", savedQuestion.getId());
            return getQuestionByIdWithOptions(savedQuestion.getId());

        } catch (Exception e) {
            log.error("Failed to duplicate question ID: {}", questionId, e);
            throw new RuntimeException("Failed to duplicate question", e);
        }
    }

    @Override
    public QuestionResponseDTO duplicateQuestionToChapter(Long questionId, Long targetChapterId) {
        log.info("Duplicating question ID: {} to chapter ID: {}", questionId, targetChapterId);

        Chapter targetChapter = chapterRepository.findById(targetChapterId)
                .orElseThrow(() -> new RecordNotFoundException("Chapter", "id", targetChapterId));

        Question originalQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", questionId));

        try {
            Question duplicatedQuestion = Question.builder()
                    .questionText(originalQuestion.getQuestionText())
                    .questionImageUrl(originalQuestion.getQuestionImageUrl())
                    .explanation(originalQuestion.getExplanation())
                    .sectionType(originalQuestion.getSectionType())
                    .questionType(originalQuestion.getQuestionType())
                    .difficultyLevel(originalQuestion.getDifficultyLevel())
                    .marks(originalQuestion.getMarks())
                    .negativeMarks(originalQuestion.getNegativeMarks())
                    .timeLimitSeconds(originalQuestion.getTimeLimitSeconds())
                    .chapter(targetChapter)
                    .isAddedToPaper(false)
                    .isActive(true)
                    .build();

            Question savedQuestion = questionRepository.save(duplicatedQuestion);

            // Duplicate MCQ options if it's an MCQ question
            if (originalQuestion.isMCQType()) {
                List<com.example.questionbank.dto.response.MCQOptionResponseDTO> originalOptions =
                        mcqOptionService.getActiveOptionsByQuestionId(questionId);

                List<com.example.questionbank.dto.request.CreateMCQOptionRequestDTO> newOptions =
                        originalOptions.stream()
                                .map(opt -> com.example.questionbank.dto.request.CreateMCQOptionRequestDTO.builder()
                                        .optionText(opt.getOptionText())
                                        .isCorrect(opt.getIsCorrect())
                                        .optionOrder(opt.getOptionOrder())
                                        .optionImageUrl(opt.getOptionImageUrl())
                                        .build())
                                .collect(Collectors.toList());

                mcqOptionService.createOptionsForQuestion(savedQuestion.getId(), newOptions);
            }

            log.info("Successfully duplicated question to new chapter. New ID: {}", savedQuestion.getId());
            return getQuestionByIdWithOptions(savedQuestion.getId());

        } catch (Exception e) {
            log.error("Failed to duplicate question ID: {} to chapter ID: {}", questionId, targetChapterId, e);
            throw new RuntimeException("Failed to duplicate question to chapter", e);
        }
    }

    @Override
    public List<QuestionResponseDTO> duplicateQuestionsToChapter(List<Long> questionIds, Long targetChapterId) {
        log.info("Duplicating {} questions to chapter ID: {}", questionIds.size(), targetChapterId);

        List<QuestionResponseDTO> duplicatedQuestions = new ArrayList<>();

        for (Long questionId : questionIds) {
            duplicatedQuestions.add(duplicateQuestionToChapter(questionId, targetChapterId));
        }

        return duplicatedQuestions;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getQuestionStatsBySubject(Long subjectId) {
        log.debug("Getting question statistics for subject ID: {}", subjectId);

        List<Question> questions = questionRepository.findActiveQuestionsBySubjectAndPaperStatus(subjectId, null);

        Map<String, Long> stats = new HashMap<>();
        stats.put("total", (long) questions.size());
        stats.put("addedToPaper", questions.stream().mapToLong(q -> q.getIsAddedToPaper() ? 1 : 0).sum());
        stats.put("notAddedToPaper", questions.stream().mapToLong(q -> !q.getIsAddedToPaper() ? 1 : 0).sum());
        stats.put("mcq", questions.stream().mapToLong(q -> q.getSectionType() == SectionType.MCQ ? 1 : 0).sum());
        stats.put("shortAnswer", questions.stream().mapToLong(q -> q.getSectionType() == SectionType.SHORT_QUESTION ? 1 : 0).sum());
        stats.put("longAnswer", questions.stream().mapToLong(q -> q.getSectionType() == SectionType.LONG_QUESTION ? 1 : 0).sum());

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getQuestionStatsByChapter(Long chapterId) {
        log.debug("Getting question statistics for chapter ID: {}", chapterId);

        List<Question> questions = questionRepository.findByChapterIdAndIsActiveTrue(chapterId);

        Map<String, Long> stats = new HashMap<>();
        stats.put("total", (long) questions.size());
        stats.put("addedToPaper", questions.stream().mapToLong(q -> q.getIsAddedToPaper() ? 1 : 0).sum());
        stats.put("notAddedToPaper", questions.stream().mapToLong(q -> !q.getIsAddedToPaper() ? 1 : 0).sum());

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getQuestionStatsByClass(Long classId) {
        log.debug("Getting question statistics for class ID: {}", classId);

        List<Question> questions = questionRepository.findActiveQuestionsByClass(classId);

        Map<String, Long> stats = new HashMap<>();
        stats.put("total", (long) questions.size());
        stats.put("addedToPaper", questions.stream().mapToLong(q -> q.getIsAddedToPaper() ? 1 : 0).sum());

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<SectionType, Long> getQuestionCountBySectionType(Long subjectId) {
        log.debug("Getting question count by section type for subject ID: {}", subjectId);

        List<Question> questions = questionRepository.findActiveQuestionsBySubjectAndPaperStatus(subjectId, null);

        return questions.stream()
                .collect(Collectors.groupingBy(
                        Question::getSectionType,
                        Collectors.counting()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<QuestionType, Long> getQuestionCountByQuestionType(Long subjectId) {
        log.debug("Getting question count by question type for subject ID: {}", subjectId);

        List<Question> questions = questionRepository.findActiveQuestionsBySubjectAndPaperStatus(subjectId, null);

        return questions.stream()
                .collect(Collectors.groupingBy(
                        Question::getQuestionType,
                        Collectors.counting()
                ));
    }

    // Simplified implementations for remaining methods
    @Override
    public List<QuestionResponseDTO> importQuestionsFromTemplate(List<Map<String, Object>> questionsData) {
        // Implementation for importing questions from template
        throw new UnsupportedOperationException("Import functionality not yet implemented");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> exportQuestionsToTemplate(List<Long> questionIds) {
        // Implementation for exporting questions to template
        throw new UnsupportedOperationException("Export functionality not yet implemented");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> exportQuestionsBySubject(Long subjectId) {
        // Implementation for exporting questions by subject
        throw new UnsupportedOperationException("Export functionality not yet implemented");
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getRandomQuestions(int count) {
        log.debug("Getting {} random questions", count);

        Pageable pageable = PageRequest.of(0, count);
        Page<Question> questions = questionRepository.findByIsActiveTrue(pageable);

        List<Question> questionList = questions.getContent();
        Collections.shuffle(questionList);

        return questionMapper.toResponseDTOListWithoutOptions(questionList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getRandomQuestionsBySubject(Long subjectId, int count) {
        log.debug("Getting {} random questions for subject ID: {}", count, subjectId);

        List<Question> allQuestions = questionRepository.findActiveQuestionsBySubjectAndPaperStatus(subjectId, null);
        Collections.shuffle(allQuestions);

        List<Question> selectedQuestions = allQuestions.stream()
                .limit(count)
                .collect(Collectors.toList());

        return questionMapper.toResponseDTOListWithoutOptions(selectedQuestions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getRandomQuestionsByChapter(Long chapterId, int count) {
        log.debug("Getting {} random questions for chapter ID: {}", count, chapterId);

        List<Question> allQuestions = questionRepository.findByChapterIdAndIsActiveTrue(chapterId);
        Collections.shuffle(allQuestions);

        List<Question> selectedQuestions = allQuestions.stream()
                .limit(count)
                .collect(Collectors.toList());

        return questionMapper.toResponseDTOListWithoutOptions(selectedQuestions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getRandomQuestionsByCriteria(
            SectionType sectionType,
            DifficultyLevel difficultyLevel,
            Long subjectId,
            int count) {

        log.debug("Getting {} random questions by criteria", count);

        Specification<Question> spec = QuestionSpecification.isActive()
                .and(QuestionSpecification.filterBySectionType(sectionType))
                .and(QuestionSpecification.filterByDifficulty(difficultyLevel))
                .and(QuestionSpecification.filterBySubject(subjectId));

        List<Question> allQuestions = questionRepository.findAll(spec);
        Collections.shuffle(allQuestions);

        List<Question> selectedQuestions = allQuestions.stream()
                .limit(count)
                .collect(Collectors.toList());

        return questionMapper.toResponseDTOListWithoutOptions(selectedQuestions);
    }

    @Override
    public void updateQuestionOrder(Long questionId, Integer newOrder) {
        // Implementation for updating question order
        log.info("Updating order for question ID: {} to order: {}", questionId, newOrder);
        // This would require adding an order field to the Question entity
        throw new UnsupportedOperationException("Question ordering not yet implemented");
    }

    @Override
    public void reorderQuestionsInChapter(Long chapterId, Map<Long, Integer> questionOrderMap) {
        // Implementation for reordering questions in chapter
        throw new UnsupportedOperationException("Question reordering not yet implemented");
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsInOrder(Long chapterId) {
        // Implementation for getting questions in order
        return getQuestionsByChapter(chapterId); // Default implementation
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDTO> advancedSearch(
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
            Pageable pageable) {

        log.debug("Performing advanced search with multiple criteria");

        Specification<Question> spec = Specification.where(null);

        if (isActive != null && isActive) {
            spec = spec.and(QuestionSpecification.isActive());
        }

        if (searchText != null && !searchText.trim().isEmpty()) {
            spec = spec.and(QuestionSpecification.searchByText(searchText));
        }

        if (isAddedToPaper != null) {
            spec = spec.and(QuestionSpecification.filterByPaperStatus(isAddedToPaper));
        }

        if (minMarks != null || maxMarks != null) {
            spec = spec.and(QuestionSpecification.filterByMarksRange(minMarks, maxMarks));
        }

        // Add specifications for list criteria
        if (sectionTypes != null && !sectionTypes.isEmpty()) {
            Specification<Question> sectionSpec = (root, query, criteriaBuilder) ->
                    root.get("sectionType").in(sectionTypes);
            spec = spec.and(sectionSpec);
        }

        if (questionTypes != null && !questionTypes.isEmpty()) {
            Specification<Question> typeSpec = (root, query, criteriaBuilder) ->
                    root.get("questionType").in(questionTypes);
            spec = spec.and(typeSpec);
        }

        if (difficultyLevels != null && !difficultyLevels.isEmpty()) {
            Specification<Question> difficultySpec = (root, query, criteriaBuilder) ->
                    root.get("difficultyLevel").in(difficultyLevels);
            spec = spec.and(difficultySpec);
        }

        Page<Question> questions = questionRepository.findAll(spec, pageable);
        return questions.map(questionMapper::toResponseDTOWithoutOptions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getRecommendedQuestions(Long questionId, int count) {
        log.debug("Getting {} recommended questions for question ID: {}", count, questionId);

        Question baseQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new RecordNotFoundException("Question", "id", questionId));

        // Simple recommendation based on same chapter and difficulty
        Specification<Question> spec = QuestionSpecification.isActive()
                .and(QuestionSpecification.filterByChapter(baseQuestion.getChapter().getId()))
                .and(QuestionSpecification.filterByDifficulty(baseQuestion.getDifficultyLevel()))
                .and((root, query, criteriaBuilder) ->
                        criteriaBuilder.notEqual(root.get("id"), questionId));

        List<Question> questions = questionRepository.findAll(spec);
        Collections.shuffle(questions);

        List<Question> recommended = questions.stream()
                .limit(count)
                .collect(Collectors.toList());

        return questionMapper.toResponseDTOListWithoutOptions(recommended);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getSimilarQuestions(Long questionId, int count) {
        // Similar to recommended questions for now
        return getRecommendedQuestions(questionId, count);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsByDifficultyProgression(Long subjectId, DifficultyLevel startLevel, int count) {
        log.debug("Getting questions by difficulty progression for subject ID: {}", subjectId);

        List<Question> allQuestions = questionRepository.findActiveQuestionsBySubjectAndPaperStatus(subjectId, null);

        // Sort by difficulty level
        List<Question> sortedQuestions = allQuestions.stream()
                .filter(q -> q.getDifficultyLevel().getLevel() >= startLevel.getLevel())
                .sorted(Comparator.comparing(q -> q.getDifficultyLevel().getLevel()))
                .limit(count)
                .collect(Collectors.toList());

        return questionMapper.toResponseDTOListWithoutOptions(sortedQuestions);
    }
}