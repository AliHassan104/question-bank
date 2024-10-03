import { Subject } from "./subject.model";


// chapter.model.ts
export interface Chapter {
    id?: number;
    name: string;
    subject: Subject;
}
  