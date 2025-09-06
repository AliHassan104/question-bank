package com.example.questionbank.model;

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

@Entity
@Table(name = "mcq_options",
        indexes = {
                @Index(name = "idx_mcq_question", columnList = "question_id"),
                @Index(name = "idx_mcq_correct", columnList = "is_correct"),
                @Index(name = "idx_mcq_order", columnList = "option_order")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"question"})
@ToString(exclude = {"question"})
public class MCQOption extends BaseEntity {

    @Column(name = "option_text", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Option text is required")
    @Size(min = 1, max = 1000, message = "Option text must be between 1 and 1000 characters")
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    @NotNull(message = "Correct status is required")
    @Builder.Default
    private Boolean isCorrect = false;

    @Column(name = "option_order")
    private Integer optionOrder;

    @Column(name = "option_image_url", length = 500)
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String optionImageUrl;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @NotNull(message = "Question is required")
    private Question question;

    // Helper methods
    public void markAsCorrect() {
        this.isCorrect = true;
    }

    public void markAsIncorrect() {
        this.isCorrect = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean hasImage() {
        return optionImageUrl != null && !optionImageUrl.trim().isEmpty();
    }
}