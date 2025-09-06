package com.example.questionbank.model;

import com.example.questionbank.model.enums.SectionType;
import com.example.questionbank.model.enums.DifficultyLevel;
import com.example.questionbank.model.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "questions",
        indexes = {
                @Index(name = "idx_question_section", columnList = "section_type"),
                @Index(name = "idx_question_type", columnList = "question_type"),
                @Index(name = "idx_question_difficulty", columnList = "difficulty_level")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"mcqOptions"})
@ToString(exclude = {"mcqOptions"})
public class Question extends BaseEntity {

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Question text is required")
    @Size(min = 10, max = 5000, message = "Question text must be between 10 and 5000 characters")
    private String questionText;

    @Column(name = "question_image_url", length = 500)
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String questionImageUrl;

    @Column(name = "explanation", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Explanation must not exceed 2000 characters")
    private String explanation;

    @Column(name = "is_added_to_paper", nullable = false)
    @Builder.Default
    private Boolean isAddedToPaper = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "section_type", nullable = false, length = 20)
    @NotNull(message = "Section type is required")
    private SectionType sectionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 20)
    @NotNull(message = "Question type is required")
    @Builder.Default
    private QuestionType questionType = QuestionType.SINGLE_CHOICE;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", length = 20)
    @Builder.Default
    private DifficultyLevel difficultyLevel = DifficultyLevel.MEDIUM;

    @Column(name = "marks", nullable = false)
    @NotNull(message = "Marks are required")
    @Builder.Default
    private Double marks = 1.0;

    @Column(name = "negative_marks")
    @Builder.Default
    private Double negativeMarks = 0.0;

    @Column(name = "time_limit_seconds")
    private Integer timeLimitSeconds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    @NotNull(message = "Chapter is required")
    private Chapter chapter;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<MCQOption> mcqOptions = new HashSet<>();

    // Transient field for report generation
    @Transient
    private List<MCQOption> transientOptions;

    // Helper methods
    public Integer getMcqOptionCount() {
        return mcqOptions != null ? mcqOptions.size() : 0;
    }

    public boolean isMCQType() {
        return sectionType == SectionType.MCQ;
    }

    public boolean isShortAnswerType() {
        return sectionType == SectionType.SHORT_QUESTION;
    }

    public boolean isLongAnswerType() {
        return sectionType == SectionType.LONG_QUESTION;
    }

    // Business logic methods
    public void togglePaperStatus() {
        this.isAddedToPaper = !this.isAddedToPaper;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    // Validation method
    public boolean isValidForPaper() {
        if (!isActive) return false;
        if (isMCQType() && (mcqOptions == null || mcqOptions.size() < 2)) return false;
        return questionText != null && !questionText.trim().isEmpty();
    }

    public boolean isAddedToPaper() {
        return Boolean.TRUE.equals(this.isAddedToPaper);
    }
}