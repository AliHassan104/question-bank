package com.example.questionbank.model.enums;

import lombok.Getter;

@Getter
public enum DifficultyLevel {
    VERY_EASY("Very Easy", 1, "#4CAF50"),
    EASY("Easy", 2, "#8BC34A"),
    MEDIUM("Medium", 3, "#FFC107"),
    HARD("Hard", 4, "#FF9800"),
    VERY_HARD("Very Hard", 5, "#F44336");

    private final String displayName;
    private final int level;
    private final String colorCode;

    DifficultyLevel(String displayName, int level, String colorCode) {
        this.displayName = displayName;
        this.level = level;
        this.colorCode = colorCode;
    }

    public static DifficultyLevel fromString(String value) {
        for (DifficultyLevel difficulty : DifficultyLevel.values()) {
            if (difficulty.name().equalsIgnoreCase(value) ||
                    difficulty.displayName.equalsIgnoreCase(value)) {
                return difficulty;
            }
        }
        throw new IllegalArgumentException("Invalid difficulty level: " + value);
    }

    public static DifficultyLevel fromLevel(int level) {
        for (DifficultyLevel difficulty : DifficultyLevel.values()) {
            if (difficulty.level == level) {
                return difficulty;
            }
        }
        throw new IllegalArgumentException("Invalid difficulty level: " + level);
    }

    public boolean isEasierThan(DifficultyLevel other) {
        return this.level < other.level;
    }

    public boolean isHarderThan(DifficultyLevel other) {
        return this.level > other.level;
    }
}