package com.example.questionbank.repository.specification;

import com.example.questionbank.model.Question;
import com.example.questionbank.model.enums.SectionType;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Join;

public class QuestionSpecification {

    public static Specification<Question> filterBySectionType(SectionType sectionType) {
        return (root, query, criteriaBuilder) -> {
            if (sectionType == null) return null;
            return criteriaBuilder.equal(root.get("sectionType"), sectionType);
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
}

