import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
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
import { log } from 'console';

@Component({
  selector: 'app-add-question',
  templateUrl: './add-question.component.html',
  styleUrls: ['./add-question.component.scss']
})
export class AddQuestionComponent implements OnInit {

  @Input() editingQuestion: Question | null = null;
  @Output() questionUpdated = new EventEmitter<void>();
  @Output() resetEditing = new EventEmitter<void>();

  classes: ClassEntity[] = [];
  subjects: Subject[] = [];
  chapters: Chapter[] = [];
  isMCQSelected: boolean = false;

  createdQuestion: Question;

  questionForm: FormGroup;

  constructor(private fb: FormBuilder,
    private classEntityService: ClassEntityService,
    private subjectService: SubjectService,
    private chapterService: ChapterService,
    private questionService: QuestionService,
    private mcqsService: MCQOptionService
  ) { }

  ngOnInit(): void {

    this.getAllClasses();
    this.getAllSubjects();
    this.getAllChapters();

    this.questionForm = this.fb.group({
      name: ['', Validators.required],
      chapter: ['', Validators.required],
      sectionType: ['', Validators.required],
      class: '',
      subject: '',
      option1: '',
      option2: '',
      option3: '',
      option4: '',
    });

    this.questionForm.get('sectionType')?.valueChanges.subscribe(value => {
      this.isMCQSelected = value === 'MCQ';
    });

    if (this.editingQuestion) {
      this.questionForm.patchValue({
        name: this.editingQuestion.questionText,
        chapter: this.editingQuestion.chapter?.id,
      });
    }
  }

  onSubmit(): void {
    if (this.questionForm.valid) {
      // Step 1: Create the question entity
      const newQuestionEntity: Question = {
        questionText: this.questionForm.value.name,
        chapter: {
          id: this.questionForm.value.chapter,
          name: '',
          subject: undefined
        },
        sectionType: this.questionForm.value.sectionType,
        isAddedToPaper: false
      };

      if (this.editingQuestion) {
        this.updateQuestionEntity(this.editingQuestion.id, newQuestionEntity);
      } else {
        this.createQuestionEntity(newQuestionEntity);
      }

    }
  }

  createMCQOptions(mcqOptions: MCQOption[]): void {
    console.log(mcqOptions);

    this.mcqsService.createMultipleMCQOptions(mcqOptions).subscribe(
      data => {
        console.log('MCQ options created successfully:', data);
        this.questionForm.reset();
      },
      error => {
        console.error('Error creating MCQ options:', error);
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

  createQuestionEntity(question: Question) {
    this.questionService.createQuestion(question).subscribe(
      data => {
        console.log('Class created successfully:', data);
        this.createdQuestion = data;
        this.questionUpdated.emit();
        debugger
        if (data.sectionType === 'MCQ') {
          let mcqOptions: MCQOption[] = [
            {
              optionText: this.questionForm.value.option1,
              question: {
                id: this.createdQuestion.id // Pass only the ID of the question
                ,
                questionText: '',
                sectionType: 'MCQ',
                isAddedToPaper: false,
                chapter: undefined
              }
            },
            {
              optionText: this.questionForm.value.option2,
              question: {
                id: this.createdQuestion.id // Pass only the ID of the question
                ,
                questionText: '',
                sectionType: 'MCQ',
                isAddedToPaper: false,
                chapter: undefined
              }
            },
            {
              optionText: this.questionForm.value.option3,
              question: {
                id: this.createdQuestion.id // Pass only the ID of the question
                ,
                questionText: '',
                sectionType: 'MCQ',
                isAddedToPaper: false,
                chapter: undefined
              }
            },
            {
              optionText: this.questionForm.value.option4,
              question: {
                id: this.createdQuestion.id,
                questionText: '',
                sectionType: 'MCQ',
                isAddedToPaper: false,
                chapter: undefined
              }
            }
          ];


          if (mcqOptions.length > 0) {
            this.createMCQOptions(mcqOptions);  // Call your API function to create MCQ options
          }
        }

        this.questionForm.reset();

      },
      error => {
        console.error('Error creating class:', error);
      }
    );
  }

  updateQuestionEntity(id: number, question: Question) {
    this.questionService.updateQuestion(id, question).subscribe(
      data => {
        console.log('Class created successfully:', data);
        this.createdQuestion = data;
        this.questionUpdated.emit(); // Notify parent to refresh list
        this.resetEditing.emit();   // Notify parent to stop editing
        this.questionForm.reset()
      },
      error => {
        console.error('Error creating class:', error);
      }
    );
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

  onSubjectChange(value: string) {
    console.log("on subject change " + value);

    if (value == 'all' || value == '') {
      this.getAllChapters();
    } else {
      this.getFilteredChapter(parseInt(value), undefined)
    }
  }

  onClassChange(value: string) {
    if (value == 'all' || value == '') {
      this.getAllSubjects();
    } else {
      this.getFilteredSubject(parseInt(value))
    }
  }


}
