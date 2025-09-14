import { ClassEntity } from "./class-entities.model";

export interface ClassSummaryDTO {
    id: number;
    name: string;
    description?: string;
}

// For API responses (what you get from backend)
export interface Subject {
    id?: number;
    name: string;
    description?: string;
    isActive?: boolean;
    classInfo: ClassEntity;
    chapterCount?: number;
    createdAt?: string;
    updatedAt?: string;
    createdBy?: string;
    updatedBy?: string;
}

// For API requests (what you send to backend)
export interface CreateSubjectRequest {
    name: string;
    description?: string;
    classId: number;
}

export interface UpdateSubjectRequest {
    name: string;
    description?: string;
    classId: number;
    isActive?: boolean;
}