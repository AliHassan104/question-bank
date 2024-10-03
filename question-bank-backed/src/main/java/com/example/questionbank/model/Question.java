package com.example.questionbank.model;

import com.example.questionbank.model.enums.SectionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;
    private Integer marks;
    private String answer;
    private boolean isAddedToPaper;

    @Enumerated(EnumType.STRING)
    private SectionType sectionType;  // Enum: MCQ, Short Answer, Long Answer

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MCQOption> mcqOptions;  // Only for MCQs

}