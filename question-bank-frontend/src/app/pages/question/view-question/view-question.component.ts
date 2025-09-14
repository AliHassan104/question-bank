import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Chapter } from 'app/models/chapter.model';
import { ClassEntity } from 'app/models/class-entities.model';
import { MCQOption } from 'app/models/mcq-option.model';
import { Question } from 'app/models/question.model';
import { Subject } from 'app/models/subject.model';
import { ChapterService } from 'app/services/chapter.service';
import { ClassEntityService } from 'app/services/class-entity.service';
import { MCQOptionService } from 'app/services/mcqoption-service.service';
import { QuestionService } from 'app/services/question.service';
import { SubjectService } from 'app/services/subject.service';

@Component({
  selector: 'app-view-question',
  templateUrl: './view-question.component.html',
  styleUrls: ['./view-question.component.scss']
})
export class ViewQuestionComponent implements OnInit {

  editingQuestion: Question | null = null;
  @Output() subjectUpdated = new EventEmitter<void>();
  @Output() resetEditing = new EventEmitter<void>();

  onChangeSectionType: string = undefined;
  onChangeClassId: number = undefined;
  onChangeSubjectId: number = undefined;
  onChangeChapterId: number = undefined;
  onChangePageSize: number = undefined;
  onChangePageNumber: number = undefined;

  classes: ClassEntity[] = [];
  subjects: Subject[] = [];
  chapters: Chapter[] = [];
  questions: Question[] = [];
  mcqOptions: { [key: number]: MCQOption[] } = {};

  constructor(
    private questionService: QuestionService,
    private mcqsService: MCQOptionService,
    private classEntityService: ClassEntityService,
    private subjectService: SubjectService,
    private chapterService: ChapterService,
  ) { }

  ngOnInit(): void {
    this.getAllQuestions(0, 10);
    this.getAllClasses();
    this.getAllSubjects();
    this.getAllChapters();
  }

  editQuestion(question: Question) {
    this.editingQuestion = question; // Fixed: was this.editQuestion = question
  }

  deleteQuestion(question: Question) {
    if (confirm('Are you sure you want to delete this question?')) {
      this.questionService.deleteQuestion(question.id).subscribe({
        next: () => {
          this.getAllQuestions(0, 10); // Refresh question list after deletion
        },
        error: (error) => {
          console.error('Error deleting question:', error);
        }
      });
    }
  }

  getAllQuestions(page: number, size: number) {
    this.questionService.getAllQuestions(page, size).subscribe({
      next: (data) => {
        console.log("get all questions ", data);
        
        this.questions = data;
        
        let questionIds = [];
        for (let i = 0; i < this.questions.length; i++) {
          if (this.questions[i].sectionType === 'MCQ') {
            questionIds.push(this.questions[i].id);
          }
        }
        
        if (questionIds.length > 0) {
          this.getOptionsByMultipleQuestionIds(questionIds);
        }
      },
      error: (error) => {
        console.error('Error fetching questions:', error);
      },
      complete: () => {
        console.log('Questions fetched successfully');
      }
    });
  }

  loadOptionsForMCQ(questionId: number): void {
    this.mcqsService.getOptionsByQuestionId(questionId).subscribe({
      next: (options: MCQOption[]) => {
        this.mcqOptions[questionId] = options;
      },
      error: (error) => {
        console.error('Error fetching MCQ options', error);
      }
    });
  }

  getOptionsByMultipleQuestionIds(questionIds: any[]) {
    console.log('Fetching options for question IDs:', questionIds);
    this.mcqsService.getOptionsByMultipleQuestionIds(questionIds).subscribe({
      next: (data) => {
        console.log('MCQ options fetched successfully:', data);
        this.mcqOptions = data;
      },
      error: (error) => {
        console.error('Error fetching MCQ options:', error);
      }
    });
  }

  addQuestionToPaper(question: Question) {
    console.log(question);
    
    this.questionService.toggleAddedToPaper(question.id).subscribe({
      next: (data) => {
        this.getAllQuestions(0, 10);
      },
      error: (error) => {
        console.error('Error toggling question paper status:', error);
      },
      complete: () => {
        console.log('Question paper status updated successfully');
      }
    });
  }

  onQuestionUpdated() {
    this.getAllQuestions(0, 10); // Refresh question list
    this.editingQuestion = null; // Reset editing mode
  }

  resetEditingMode() {
    this.editingQuestion = null;
  }

  onSectionTypeChange(value: string) {
    this.onChangeSectionType = value === '' ? undefined : value;
    this.filterQuestion();
  }

  onClassChange(value: string) {
    this.onChangeClassId = value === '' ? undefined : parseInt(value);
    if (value === '' || !value) {
      this.getAllSubjects();
    } else {
      this.getFilteredSubject(parseInt(value));
    }
    this.filterQuestion();
  }

  onSubjectChange(value: string) {
    this.onChangeSubjectId = value === '' ? undefined : parseInt(value);
    if (value === '' || !value) {
      this.getAllChapters();
    } else {
      this.getFilteredChapter(parseInt(value), undefined);
    }
    this.filterQuestion();
  }

  onChapterChange(value: string) {
    this.onChangeChapterId = value === '' ? undefined : parseInt(value);
    this.filterQuestion();
  }

  filterQuestion() {
    this.getFilteredQuestions(
      this.onChangeSectionType, 
      this.onChangeChapterId, 
      this.onChangeSubjectId, 
      this.onChangeClassId, 
      this.onChangePageNumber || 0, 
      this.onChangePageSize || 10
    );
  }

  getFilteredSubject(classId: number) {
    this.subjectService.getSubjectsByClass(classId).subscribe({
      next: (data) => {
        this.subjects = data;
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

  getFilteredQuestions(sectionType: string, chapterId: number, subjectId: number, classId: number, page: number, size: number) {
    this.questionService.getFilteredQuestions(sectionType, chapterId, subjectId, classId, page, size).subscribe({
      next: (data) => {
        // Filter out questions with incomplete data
        this.questions = data.content.filter(question => 
          question && 
          question.questionText && 
          question.chapterInfo && 
          question.chapterInfo.subjectInfo
        );
        
        // Load MCQ options for filtered questions
        let questionIds = [];
        for (let question of this.questions) {
          if (question.sectionType === 'MCQ') {
            questionIds.push(question.id);
          }
        }
        
        if (questionIds.length > 0) {
          this.getOptionsByMultipleQuestionIds(questionIds);
        }
      },
      error: (error) => {
        console.error('Error fetching filtered questions:', error);
      }
    });
  }

  getAllClasses() {
    this.classEntityService.getAllActiveClasses().subscribe({
      next: (data) => {
        // Filter out invalid class objects
        this.classes = data.filter(classEntity => classEntity && classEntity.name);
      },
      error: (error) => {
        console.error('Error fetching classes:', error);
      },
      complete: () => {
        console.log('Classes fetch completed');
      }
    });
  }

  getAllSubjects() {
    this.subjectService.getAllSubjects().subscribe({
      next: (data) => {
        // Filter out invalid subject objects
        this.subjects = data.filter(subject => subject && subject.name);
      },
      error: (error) => {
        console.error('Error fetching subjects:', error);
      },
      complete: () => {
        console.log('Subjects fetch completed');
      }
    });
  }

  getAllChapters() {
    this.chapterService.getAllChapters().subscribe({
      next: (data) => {
        // Filter out invalid chapter objects - handle both subject and subjectInfo
        this.chapters = data.filter(chapter => 
          chapter && 
          chapter.name && 
          (chapter.subjectInfo || chapter.subjectInfo)
        );
      },
      error: (error) => {
        console.error('Error fetching chapters:', error);
      },
      complete: () => {
        console.log('Chapters fetch completed');
      }
    });
  }
}