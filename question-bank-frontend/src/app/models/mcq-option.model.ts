import { Question } from "./question.model";

export interface MCQOption {
    id?: number;
    optionText: string;
    isCorrect?: boolean;
    optionOrder?: number;
    optionImageUrl?: string;
    isActive?: boolean;
    question: Question; // Link to the related question
}