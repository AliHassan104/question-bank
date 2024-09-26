export interface MCQOption {
    id?: number;
    optionText: string;
    isCorrect: boolean; // Assuming there's a field to indicate if the option is correct
    questionId: number; // Link to the related question
  }
  