package com.example.questionbank.repository.specification;

import com.example.questionbank.model.Question;
import com.example.questionbank.model.enums.SectionType;
import com.example.questionbank.model.enums.QuestionType;
import com.example.questionbank.model.enums.DifficultyLevel;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;

public class QuestionSpecification {

    public static Specification<Question> isActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("isActive"));
    }

    public static Specification<Question> filterBySectionType(SectionType sectionType) {
        return (root, query, criteriaBuilder) -> {
            if (sectionType == null) return null;
            return criteriaBuilder.equal(root.get("sectionType"), sectionType);
        };
    }

    public static Specification<Question> filterByQuestionType(QuestionType questionType) {
        return (root, query, criteriaBuilder) -> {
            if (questionType == null) return null;
            return criteriaBuilder.equal(root.get("questionType"), questionType);
        };
    }

    public static Specification<Question> filterByDifficulty(DifficultyLevel difficultyLevel) {
        return (root, query, criteriaBuilder) -> {
            if (difficultyLevel == null) return null;
            return criteriaBuilder.equal(root.get("difficultyLevel"), difficultyLevel);
        };
    }

    public static Specification<Question> filterByChapter(Long chapterId) {
        return (root, query, criteriaBuilder) -> {
            if (chapterId == null) return null;
            return criteriaBuilder.equal(root.get("chapter").get("id"), chapterId);
        };
    }

    public static Specification<Question> filterBySubject(Long subjectId) {
        return (root, query, criteriaBuilder) -> {
            if (subjectId == null) return null;
            Join<Object, Object> chapterJoin = root.join("chapter");
            return criteriaBuilder.equal(chapterJoin.get("subject").get("id"), subjectId);
        };
    }

    public static Specification<Question> filterByClass(Long classId) {
        return (root, query, criteriaBuilder) -> {
            if (classId == null) return null;
            Join<Object, Object> chapterJoin = root.join("chapter");
            Join<Object, Object> subjectJoin = chapterJoin.join("subject");
            return criteriaBuilder.equal(subjectJoin.get("classEntity").get("id"), classId);
        };
    }

    public static Specification<Question> filterByPaperStatus(Boolean isAddedToPaper) {
        return (root, query, criteriaBuilder) -> {
            if (isAddedToPaper == null) return null;
            return criteriaBuilder.equal(root.get("isAddedToPaper"), isAddedToPaper);
        };
    }

    public static Specification<Question> filterByMarksRange(Double minMarks, Double maxMarks) {
        return (root, query, criteriaBuilder) -> {
            if (minMarks == null && maxMarks == null) return null;
            if (minMarks != null && maxMarks != null) {
                return criteriaBuilder.between(root.get("marks"), minMarks, maxMarks);
            } else if (minMarks != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("marks"), minMarks);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("marks"), maxMarks);
            }
        };
    }

    public static Specification<Question> searchByText(String searchText) {
        return (root, query, criteriaBuilder) -> {
            if (searchText == null || searchText.trim().isEmpty()) return null;
            String likePattern = "%" + searchText.toLowerCase() + "%";
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("questionText")),
                    likePattern
            );
        };
    }
}