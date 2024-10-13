import { Question } from "./question.model";

export interface MCQOption {
    id?: number;
    optionText: string;
    question: Question; // Link to the related question
  }
  