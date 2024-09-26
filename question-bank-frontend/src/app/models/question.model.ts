export interface Question {
    id?: number;
    questionText: string;
    sectionType: SectionType;
    // Add other fields as necessary
  }
  
  export enum SectionType {
    MCQ = 'MCQ',
    SHORT = 'SHORT',
    LONG = 'LONG',
    // Add other section types as needed
  }