import { ClassEntity } from "./class-entities.model";

export interface Subject {
    id?: number;
    name: string;
    classEntity: ClassEntity;
  }