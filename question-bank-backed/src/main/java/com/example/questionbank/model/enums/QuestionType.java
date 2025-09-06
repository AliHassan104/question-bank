package com.example.questionbank.model.enums;

import lombok.Getter;

@Getter
public enum QuestionType {
    SINGLE_CHOICE("Single Choice", "Only one correct answer"),
    MULTIPLE_CHOICE("Multiple Choice", "Multiple correct answers allowed"),
    TRUE_FALSE("True/False", "Binary choice question"),
    FILL_BLANK("Fill in Blank", "Fill in the missing word(s)"),
    SHORT_ANSWER("Short Answer", "Brief written response"),
    LONG_ANSWER("Long Answer", "Detailed written response"),
    ESSAY("Essay", "Extended written response"),
    NUMERICAL("Numerical", "Numerical answer required"),
    MATCHING("Matching", "Match items from two lists");

    private final String displayName;
    private final String description;

    QuestionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static QuestionType fromString(String value) {
        for (QuestionType type : QuestionType.values()) {
            if (type.name().equalsIgnoreCase(value) ||
                    type.displayName.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid question type: " + value);
    }

    public boolean requiresOptions() {
        return this == SINGLE_CHOICE || this == MULTIPLE_CHOICE || this == MATCHING;
    }

    public boolean allowsMultipleCorrectAnswers() {
        return this == MULTIPLE_CHOICE;
    }
}