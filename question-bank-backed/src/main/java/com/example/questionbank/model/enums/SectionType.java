package com.example.questionbank.model.enums;

import lombok.Getter;

@Getter
public enum SectionType {
    MCQ("Multiple Choice Questions", "MCQ"),
    SHORT_QUESTION("Short Answer Questions", "Short"),
    LONG_QUESTION("Long Answer Questions", "Long"),
    TRUE_FALSE("True/False Questions", "T/F"),
    FILL_IN_BLANK("Fill in the Blanks", "Fill"),
    ESSAY("Essay Questions", "Essay");

    private final String displayName;
    private final String shortName;

    SectionType(String displayName, String shortName) {
        this.displayName = displayName;
        this.shortName = shortName;
    }

    public static SectionType fromString(String value) {
        for (SectionType type : SectionType.values()) {
            if (type.name().equalsIgnoreCase(value) ||
                    type.displayName.equalsIgnoreCase(value) ||
                    type.shortName.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid section type: " + value);
    }
}