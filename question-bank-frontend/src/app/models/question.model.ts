import { Chapter } from "./chapter.model";

export interface Question {
    id?: number;
    questionText: string;
    sectionType: string;
    isAddedToPaper: boolean;
    chapter: Chapter;
}
  