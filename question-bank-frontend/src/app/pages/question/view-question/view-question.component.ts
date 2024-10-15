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

  constructor(private questionService: QuestionService,
    private mcqsService: MCQOptionService,
    private classEntityService: ClassEntityService,
    private subjectService: SubjectService,
    private chapterService: ChapterService,

  ) { }

  ngOnInit(): void {
    this.getAllQuestions(0, 10)

    this.getAllClasses();
    this.getAllSubjects();
    this.getAllChapters();

  }

  questions: Question[] = [];
  mcqOptions: { [key: number]: MCQOption[] } = {};

  editQuestion(question: any) {
    this.editQuestion = question;
  }

  deleteQuestion(question: Question) {
    this.questionService.deleteQuestion(question.id).subscribe(
      () => {
        this.getAllQuestions(0, 10); // Refresh subject list after deletion
      },
      error => {
        console.error('Error deleting subject:', error);
      }
    );
  }

  getAllQuestions(page: number, size: number) {
    this.questionService.getAllQuestions(page, size).subscribe({
      next: data => {

        let questionIds = []

        this.questions = data.content;

        for (let i = 0; i < this.questions.length; i++) {

          if (this.questions[i].sectionType === 'MCQ') {
            questionIds.push(this.questions[i].id);
          }

        }

        this.getOptionsByMultipleQuestionIds(questionIds)

      },
      error: error => {
        console.error('Error fetching questions:', error);
      },
      complete: () => {
        console.log('Questions fetched successfully');
      }
    });
  }

  loadOptionsForMCQ(questionId: number): void {
    this.mcqsService.getOptionsByQuestionId(questionId).subscribe(
      (options: MCQOption[]) => {
        this.mcqOptions[questionId] = options;
      },
      error => {
        console.error('Error fetching MCQ options', error);
      }
    );
  }

  getOptionsByMultipleQuestionIds(questionIds: any[]) {
    console.log(questionIds);
    this.mcqsService.getOptionsByMultipleQuestionIds(questionIds).subscribe(
      data => {
        console.log('MCQ options get successfully by question id:', data);

        this.mcqOptions = data;
        // this.questionForm.reset();
      },
      error => {
        console.error('Error creating MCQ options:', error);
      }
    )
  }

  addQuestionToPaper(question: Question) {
    this.questionService.toggleAddedToPaper(question.id).subscribe({
      next: data => {
        this.getAllQuestions(0, 10)
      },
      error: error => {
        console.error('Error fetching questions:', error);
      },
      complete: () => {
        console.log('Questions fetched successfully');
      }
    });
  }

  onQuestionUpdated() {
    this.getAllQuestions(0, 10); // Refresh subject list
    this.editQuestion = null; // Reset editing mode
  }

  resetEditingMode() {
    this.editQuestion = null;
  }

  onSectionTypeChange(value: string) {
    this.onChangeSectionType = value
    if (value == 'all' || value == '') {
      this.getAllSubjects();
    } else {

    }
    this.filterQuestion()
  }

  onClassChange(value: string) {
    this.onChangeClassId = parseInt(value);
    if (value == 'all' || value == '') {
      this.getAllSubjects();
    } else {
      this.getFilteredSubject(parseInt(value));
    }
    this.filterQuestion()
  }

  onSubjectChange(value: string) {

    this.onChangeSubjectId = parseInt(value);

    if (value == 'all' || value == '') {
      this.getAllChapters();
    } else {
      this.getFilteredChapter(parseInt(value), undefined)
    }
    this.filterQuestion()
  }

  onChapterChange(value: string) {
    this.onChangeChapterId = parseInt(value);
    if (value == 'all' || value == '') {
      this.getAllQuestions(0, 10);
    } else {

    }
    this.filterQuestion()
  }

  filterQuestion() {
    this.getFilteredQuestions(this.onChangeSectionType, this.onChangeChapterId, this.onChangeSubjectId, this.onChangeClassId, this.onChangePageNumber, this.onChangePageSize);
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
        this.chapters = data;
      },
      error => {
        console.error('Error fetching classes:', error);
      }
    );
  }

  getFilteredQuestions(sectionType: string, chapterId: number, subjectId: number, classId: number, page: number, size: number) {
    this.questionService.getFilteredQuestions(sectionType, chapterId, subjectId, classId, page, size).subscribe(
      (data) => {
        this.questions = data.content
      },
      (error) => {
        console.error('Error fetching questions:', error); // Handle error
      }
    );
  }

  getAllClasses() {
    this.classEntityService.getAllClassEntities().subscribe({
      next: (data) => {
        this.classes = data;
      },
      error: (error) => {
        console.error('Error fetching classes:', error);
      },
      complete: () => {
        console.log('Fetch completed');
      }
    });
  }

  getAllSubjects() {
    this.subjectService.getAllSubjects().subscribe({
      next: (data) => {
        this.subjects = data;
      },
      error: (error) => {
        console.error('Error fetching subjects:', error);
      },
      complete: () => {
        console.log('Fetch completed');
      }
    });
  }

  getAllChapters() {
    this.chapterService.getAllChapters().subscribe({
      next: (data) => {
        this.chapters = data;
      },
      error: (error) => {
        console.error('Error fetching chapters:', error);
      },
      complete: () => {
        console.log('Fetch completed');
      }
    });
  }
}
