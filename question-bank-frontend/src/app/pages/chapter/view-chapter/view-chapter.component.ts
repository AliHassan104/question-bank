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
        console.log('Chapter deleted successfully');
        this.getAllChapters(); // Refresh the chapters list
      },
      error: (error) => {
        console.error('Error deleting chapter:', error);
      }
    });
  }

  getAllChapters(): void {
    this.chapterService.getAllChapters().subscribe({
      next: (data) => {
        this.chapters = data;
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
    console.log("on class change " + value);

    if (value == 'all' || value == '') {
      this.getAllSubjects();
      this.getAllChapters();
    } else {
      this.getFilteredChapter(undefined, parseInt(value));
      this.getFilteredSubject(parseInt(value))
    }
  }

  onSubjectChange(value: string) {
    console.log("on subject change " + value);

    if (value == 'all' || value == '') {
      this.getAllChapters();
    } else {
      this.getFilteredChapter(parseInt(value), undefined)
    }
  }

  getAllClasses(): void {
    this.classEntityService.getAllClassEntities().subscribe({
      next: (data) => {
        this.classes = data;
      },
      error: (error) => {
        console.error('Error fetching classes:', error);
      }
    });
  }

  getAllSubjects(): void {
    this.subjectService.getAllSubjects().subscribe({
      next: (data) => {
        this.subjects = data;
      },
      error: (error) => {
        console.error('Error fetching subjects:', error);
      }
    });
  }


  getFilteredSubject(classId: number) {
    this.subjectService.filterSubjectsByClass(classId).subscribe(
      data => {
        this.subjects = data;
      },
      error => {
        console.error('Error fetching classes:', error);
      }
    );
  }

  getFilteredChapter(subjectId: number, classId: number) {
    this.chapterService.filterChapters(subjectId, classId).subscribe(
      data => {
        console.log("class id " + classId);
        console.log("subject id" + subjectId);

        console.log(data);

        this.chapters = data;
      },
      error => {
        console.error('Error fetching classes:', error);
      }
    );
  }
}
