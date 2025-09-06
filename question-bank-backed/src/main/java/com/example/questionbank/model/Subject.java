package com.example.questionbank.model;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subjects",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "class_id"}),
        indexes = @Index(name = "idx_subject_name", columnList = "name"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"chapters"})
@ToString(exclude = {"chapters"})
public class Subject extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Subject name is required")
    @Size(min = 1, max = 100, message = "Subject name must be between 1 and 100 characters")
    private String name;

    @Column(name = "description", length = 500)
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Chapter> chapters = new HashSet<>();

    // Helper method
    public Integer getChapterCount() {
        return chapters != null ? chapters.size() : 0;
    }
}
