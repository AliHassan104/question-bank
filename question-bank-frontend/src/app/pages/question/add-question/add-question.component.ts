import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Chapter } from 'app/models/chapter.model';
import { ClassEntity } from 'app/models/class-entities.model';
import { Question } from 'app/models/question.model';
import { Subject } from 'app/models/subject.model';
import { ChapterService } from 'app/services/chapter.service';
import { ClassEntityService } from 'app/services/class-entity.service';
import { QuestionService } from 'app/services/question.service';
import { SubjectService } from 'app/services/subject.service';

@Component({
  selector: 'app-add-question',
  templateUrl: './add-question.component.html',
  styleUrls: ['./add-question.component.scss']
})
export class AddQuestionComponent implements OnInit {

  classes: ClassEntity[] = [];
  subjects: Subject[] = [];
  chapters: Chapter[] = [];
  isMCQSelected: boolean = false;  // Flag for checking MCQ selection

  questionForm: FormGroup;

  constructor(private fb: FormBuilder,
    private classEntityService: ClassEntityService,
    private subjectService: SubjectService,
    private chapterService: ChapterService,
    private questionService: QuestionService
  ) { }

  ngOnInit(): void {

    this.getAllClasses(0, 10);
    this.getAllSubjects(0, 10);
    this.getAllChapters(0, 10);

    this.questionForm = this.fb.group({
      name: ['', Validators.required],
      chapter: ['', Validators.required],
      sectionType: ['', Validators.required],
      class: '', 
      subject: '', 
      options: this.fb.array([]) // FormArray for MCQ options
    });

    // Listen for sectionType changes
    this.questionForm.get('sectionType')?.valueChanges.subscribe(value => {
      this.isMCQSelected = value === 'MCQ';
      if (this.isMCQSelected) {
        this.addMCQOptions();
      } else {
        this.clearMCQOptions();
      }
    });
  }

  // Get form array of MCQ options
  get options(): FormArray {
    return this.questionForm.get('options') as FormArray;
  }

  // Add 4 blank options for MCQs
  addMCQOptions(): void {
    this.clearMCQOptions();  // Clear any existing options before adding
    for (let i = 0; i < 4; i++) {
      this.options.push(this.fb.control('', Validators.required));
    }
  }

  // Clear all options when MCQ is deselected
  clearMCQOptions(): void {
    this.options.clear();
  }

  onSubmit(): void {
    if (this.questionForm.valid) {
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

      if (this.isMCQSelected) {
        newQuestionEntity['options'] = this.questionForm.value.options;  // Add options if MCQ
      }

      this.createQuestionEntity(newQuestionEntity);
      this.questionForm.reset();
    }
  }

  getAllClasses(page: number, size: number) {
    this.classEntityService.getAllClassEntities(page, size).subscribe({
      next: (data) => {
        console.log(data);
        
        this.classes = data.content;
      },
      error: (error) => {
        console.error('Error fetching classes:', error);
      },
      complete: () => {
        console.log('Fetch completed');
      }
    });
  }

  getAllSubjects(page: number, size: number) {
    this.subjectService.getAllSubjects(page, size).subscribe({
      next: (data) => {
        console.log(data);
        
        this.subjects = data.content;
      },
      error: (error) => {
        console.error('Error fetching subjects:', error);
      },
      complete: () => {
        console.log('Fetch completed');
      }
    });
  }
  

  getAllChapters(page: number, size: number) {
    this.chapterService.getAllChapters(page, size).subscribe({
      next: (data) => {
        console.log(data);

        this.chapters = data.content;
      },
      error: (error) => {
        console.error('Error fetching chapters:', error);
      },
      complete: () => {
        console.log('Fetch completed');
      }
    });
  }

  createQuestionEntity( question: Question) {
    this.questionService.createQuestion(question).subscribe(
      data => {
        console.log('Class created successfully:', data);
      },
      error => {
        console.error('Error creating class:', error);
      }
    );
  }

}
