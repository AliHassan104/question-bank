import { Chapter } from "./chapter.model";

export interface Question {
    id?: number;
    questionText: string;
    sectionType: string;
    questionType?: string;
    difficultyLevel?: string;
    marks?: number;
    negativeMarks?: number;
    timeLimitSeconds?: number;
    isAddedToPaper: boolean;
    isActive?: boolean;
    explanation?: string;
    questionImageUrl?: string;
    chapterInfo: Chapter;
}

// Enums to match backend
export enum SectionType {
    MCQ = "MCQ",
    SHORT_QUESTION = "SHORT_QUESTION", 
    LONG_QUESTION = "LONG_QUESTION",
    TRUE_FALSE = "TRUE_FALSE",
    FILL_IN_BLANK = "FILL_IN_BLANK",
    ESSAY = "ESSAY"
}

export enum QuestionType {
    SINGLE_CHOICE = "SINGLE_CHOICE",
    MULTIPLE_CHOICE = "MULTIPLE_CHOICE",
    TRUE_FALSE = "TRUE_FALSE",
    FILL_IN_BLANK = "FILL_IN_BLANK"
}

export enum DifficultyLevel {
    EASY = "EASY",
    MEDIUM = "MEDIUM", 
    HARD = "HARD"
}