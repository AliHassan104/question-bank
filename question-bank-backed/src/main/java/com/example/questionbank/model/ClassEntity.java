package com.example.questionbank.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "classes",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"),
        indexes = @Index(name = "idx_class_name", columnList = "name"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"subjects"})
@ToString(exclude = {"subjects"})
public class ClassEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Class name is required")
    @Size(min = 1, max = 100, message = "Class name must be between 1 and 100 characters")
    private String name;

    @Column(name = "description", length = 500)
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Subject> subjects = new HashSet<>();

    // Helper method to get subject count
    public Integer getSubjectCount() {
        return subjects != null ? subjects.size() : 0;
    }
}