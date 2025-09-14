import { Component, OnInit } from '@angular/core';
import { Chapter } from 'app/models/chapter.model';
import { ClassEntity } from 'app/models/class-entities.model';
import { Subject } from 'app/models/subject.model';
import { ChapterService } from 'app/services/chapter.service';
import { ClassEntityService } from 'app/services/class-entity.service';
import { SubjectService } from 'app/services/subject.service';

@Component({
  selector: 'app-view-chapter',
  templateUrl: './view-chapter.component.html',
  styleUrls: ['./view-chapter.component.scss']
})
export class ViewChapterComponent implements OnInit {

  chapters: Chapter[] = [];
  selectedChapter: Chapter | null = null;
  classes: ClassEntity[] = [];
  subjects: Subject[] = [];

  constructor(private chapterService: ChapterService, private classEntityService: ClassEntityService, private subjectService: SubjectService) { }

  ngOnInit(): void {
    this.getAllChapters();
    this.getAllSubjects();
    this.getAllClasses();
  }

  editChapter(chapter: Chapter): void {
    this.selectedChapter = chapter; // Pass the chapter to the form for editing
  }

  deleteChapter(chapter: Chapter): void {
    this.chapterService.deleteChapter(chapter.id).subscribe({
      next: () => {
        this.getAllChapters(); // Refresh the chapters list
      },
      error: (error) => {
        console.error('Error deleting chapter:', error);
      }
    });
  }

  getAllChapters(): void {
    this.chapterService.getAllActiveChapters().subscribe({
      next: (data) => {
        // Filter out any invalid chapter objects
        this.chapters = data.filter(chapter => chapter && chapter.name);
      },
      error: (error) => {
        console.error('Error fetching chapters:', error);
      }
    });
  }

  onChapterAdded(): void {
    this.getAllChapters(); // Refresh the list when a chapter is added or edited
    this.selectedChapter = null; // Reset the selected chapter after editing
  }

  onClassChange(value: string) {

    if (value == 'all' || value == '') {
      this.getAllSubjects();
      this.getAllChapters();
    } else {
      this.getFilteredChapter(undefined, parseInt(value));
      this.getFilteredSubject(parseInt(value))
    }
  }

  onSubjectChange(value: string) {

    if (value == 'all' || value == '') {
      this.getAllChapters();
    } else {
      this.getFilteredChapter(parseInt(value), undefined)
    }
  }

  getAllClasses(): void {
    this.classEntityService.getAllActiveClasses().subscribe({
      next: (data) => {
        // Filter out any invalid class objects
        this.classes = data.filter(classEntity => classEntity && classEntity.name && classEntity.name.trim() !== '');
      },
      error: (error) => {
        console.error('Error fetching classes:', error);
      }
    });
  }

  getAllSubjects(): void {
    this.subjectService.getAllActiveSubjects().subscribe({
      next: (data) => {
        // Filter out any invalid subject objects
        this.subjects = data.filter(subjectEntity => subjectEntity && subjectEntity.name && subjectEntity.name.trim() !== '');
      },
      error: (error) => {
        console.error('Error fetching subjects:', error);
      }
    });
  }

  getFilteredSubject(classId: number) {
    this.subjectService.getSubjectsByClass(classId).subscribe({
      next: (data) => {
        // Filter out any invalid subject objects
        this.subjects = data.filter(subjectEntity => subjectEntity && subjectEntity.name && subjectEntity.name.trim() !== '');
      },
      error: (error) => {
        console.error('Error fetching filtered subjects:', error);
      }
    });
  }

  getFilteredChapter(subjectId: number, classId: number) {
    this.chapterService.filterChapters(subjectId, classId).subscribe({
      next: (data) => {
        this.chapters = data;
      },
      error: (error) => {
        console.error('Error fetching filtered chapters:', error);
      }
    });
  }
}