package com.example.questionbank.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "chapters",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "subject_id"}),
        indexes = @Index(name = "idx_chapter_name", columnList = "name"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"questions"})
@ToString(exclude = {"questions"})
public class Chapter extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Chapter name is required")
    @Size(min = 1, max = 100, message = "Chapter name must be between 1 and 100 characters")
    private String name;

    @Column(name = "description", length = 500)
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Question> questions = new HashSet<>();

    // Helper method
    public Integer getQuestionCount() {
        return questions != null ? questions.size() : 0;
    }
}

