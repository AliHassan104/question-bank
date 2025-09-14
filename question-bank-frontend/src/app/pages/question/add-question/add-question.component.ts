import { Component, EventEmitter, Input, OnInit, Output, OnChanges } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
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
  selector: 'app-add-question',
  templateUrl: './add-question.component.html',
  styleUrls: ['./add-question.component.scss']
})
export class AddQuestionComponent implements OnInit, OnChanges {

  @Input() editingQuestion: Question | null = null;
  @Output() questionUpdated = new EventEmitter<void>();
  @Output() resetEditing = new EventEmitter<void>();

  classes: ClassEntity[] = [];
  subjects: Subject[] = [];
  chapters: Chapter[] = [];
  isMCQSelected: boolean = false;

  createdQuestion: Question;
  questionForm: FormGroup;

  // Added properties for better UX
  submitted: boolean = false;
  loading: boolean = false;
  loadingSubjects: boolean = false;
  loadingChapters: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private classEntityService: ClassEntityService,
    private subjectService: SubjectService,
    private chapterService: ChapterService,
    private questionService: QuestionService,
    private mcqsService: MCQOptionService
  ) {
    this.initializeForm();
  }

  ngOnInit(): void {
    this.getAllClasses();
    this.getAllSubjects();
    this.getAllChapters();
    this.setupFormSubscriptions();
  }

  ngOnChanges(): void {
    if (this.editingQuestion && this.questionForm) {
      this.populateFormForEdit();
    } else if (this.questionForm) {
      this.questionForm.reset();
    }
    // Clear messages when switching modes
    this.clearMessages();
  }

  private initializeForm(): void {
    this.questionForm = this.fb.group({
      name: ['', Validators.required],
      chapter: ['', Validators.required],
      sectionType: ['', Validators.required],
      class: [''],
      subject: [''],
      option1: [''],
      option2: [''],
      option3: [''],
      option4: [''],
    });
  }

  private setupFormSubscriptions(): void {
    this.questionForm.get('sectionType')?.valueChanges.subscribe(value => {
      this.isMCQSelected = value === 'MCQ';
      
      // Add validation for MCQ options when MCQ is selected
      if (this.isMCQSelected) {
        this.questionForm.get('option1')?.setValidators([Validators.required]);
        this.questionForm.get('option2')?.setValidators([Validators.required]);
      } else {
        this.questionForm.get('option1')?.clearValidators();
        this.questionForm.get('option2')?.clearValidators();
        this.questionForm.get('option3')?.clearValidators();
        this.questionForm.get('option4')?.clearValidators();
      }
      
      this.questionForm.get('option1')?.updateValueAndValidity();
      this.questionForm.get('option2')?.updateValueAndValidity();
      this.questionForm.get('option3')?.updateValueAndValidity();
      this.questionForm.get('option4')?.updateValueAndValidity();
    });
  }

  private populateFormForEdit(): void {
    if (this.editingQuestion) {
      this.questionForm.patchValue({
        name: this.editingQuestion.questionText,
        chapter: this.editingQuestion.chapterInfo?.id,
        sectionType: this.editingQuestion.sectionType,
        class: this.editingQuestion.chapterInfo?.subjectInfo?.classInfo?.id || this.editingQuestion.chapterInfo?.subjectInfo?.classInfo?.id,
        subject: this.editingQuestion.chapterInfo?.subjectInfo?.id
      });
      
      // Set the section type selection
      this.isMCQSelected = this.editingQuestion.sectionType === 'MCQ';
    }
  }

  // Added: Fixed validation check method for chapter
  shouldShowChapterError(): boolean {
    const chapterControl = this.questionForm.get('chapter');
    if (!chapterControl) return false;
    
    return (this.submitted && chapterControl.invalid) || 
           (chapterControl.touched && chapterControl.invalid && !chapterControl.value);
  }

  // Added: Getter for form controls
  get f() {
    return this.questionForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;
    this.clearMessages();

    // Explicit check for chapter selection
    if (!this.questionForm.get('chapter')?.value) {
      this.errorMessage = 'Please select a chapter before submitting.';
      return;
    }

    if (this.questionForm.valid) {
      this.loading = true;
      
      // Create the request DTO that matches backend expectations
      const questionRequest = {
        questionText: this.questionForm.value.name.trim(),
        chapterId: parseInt(this.questionForm.value.chapter),
        sectionType: this.questionForm.value.sectionType,
        questionType: 'SINGLE_CHOICE', // Default question type
        difficultyLevel: 'MEDIUM', // Default difficulty
        marks: 1.0, // Default marks
        negativeMarks: 0.0,
        isActive: true,
        isAddedToPaper: false
      };

      if (this.editingQuestion) {
        this.updateQuestionEntity(this.editingQuestion.id, questionRequest);
      } else {
        this.createQuestionEntity(questionRequest);
      }
    } else {
      this.errorMessage = 'Please fill in all required fields correctly.';
      this.markFormGroupTouched();
    }
  }

  createQuestionEntity(questionRequest: any): void {
    this.questionService.createQuestion(questionRequest).subscribe({
      next: (data) => {
        console.log('Question created successfully:', data);
        this.createdQuestion = data;
        this.loading = false;
        this.submitted = false;
        this.successMessage = 'Question created successfully!';
        this.questionUpdated.emit();
        
        // Create MCQ options if this is an MCQ question
        if (data.sectionType === 'MCQ') {
          this.createMCQOptionsForQuestion(data.id);
        } else {
          this.questionForm.reset();
        }
        
        // Clear success message after 3 seconds
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error creating question:', error);
        this.loading = false;
        this.handleError(error);
      }
    });
  }

  updateQuestionEntity(id: number, questionRequest: any): void {
    this.questionService.updateQuestion(id, questionRequest).subscribe({
      next: (data) => {
        console.log('Question updated successfully:', data);
        this.loading = false;
        this.submitted = false;
        this.successMessage = 'Question updated successfully!';
        this.questionUpdated.emit();
        this.resetEditing.emit();
        this.questionForm.reset();
        
        // Clear success message after 3 seconds
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error updating question:', error);
        this.loading = false;
        this.handleError(error);
      }
    });
  }

  private createMCQOptionsForQuestion(questionId: number): void {
    const mcqOptions: any[] = [];
    
    // Only add non-empty options
    if (this.questionForm.value.option1?.trim()) {
      mcqOptions.push({
        optionText: this.questionForm.value.option1.trim(),
        questionId: questionId,
        isCorrect: false,
        optionOrder: 1,
        isActive: true
      });
    }
    
    if (this.questionForm.value.option2?.trim()) {
      mcqOptions.push({
        optionText: this.questionForm.value.option2.trim(),
        questionId: questionId,
        isCorrect: false,
        optionOrder: 2,
        isActive: true
      });
    }
    
    if (this.questionForm.value.option3?.trim()) {
      mcqOptions.push({
        optionText: this.questionForm.value.option3.trim(),
        questionId: questionId,
        isCorrect: false,
        optionOrder: 3,
        isActive: true
      });
    }
    
    if (this.questionForm.value.option4?.trim()) {
      mcqOptions.push({
        optionText: this.questionForm.value.option4.trim(),
        questionId: questionId,
        isCorrect: false,
        optionOrder: 4,
        isActive: true
      });
    }

    if (mcqOptions.length > 0) {
      this.createMCQOptions(mcqOptions);
    } else {
      this.questionForm.reset();
    }
  }

  createMCQOptions(mcqOptions: any[]): void {
    console.log('Creating MCQ options:', mcqOptions);

    this.mcqsService.createMultipleMCQOptions(mcqOptions).subscribe({
      next: (data) => {
        console.log('MCQ options created successfully:', data);
        this.questionForm.reset();
        this.submitted = false;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error creating MCQ options:', error);
        this.handleError(error);
      }
    });
  }

  getAllClasses(): void {
    this.classEntityService.getAllActiveClasses().subscribe({
      next: (data) => {
        this.classes = data;
      },
      error: (error) => {
        console.error('Error fetching classes:', error);
        this.errorMessage = 'Failed to load classes. Please refresh the page.';
      }
    });
  }

  getAllSubjects(): void {
    this.subjectService.getAllActiveSubjects().subscribe({
      next: (data) => {
        this.subjects = data;
      },
      error: (error) => {
        console.error('Error fetching subjects:', error);
        this.errorMessage = 'Failed to load subjects. Please refresh the page.';
      }
    });
  }

  getAllChapters(): void {
    this.chapterService.getAllActiveChapters().subscribe({
      next: (data) => {
        this.chapters = data;
      },
      error: (error) => {
        console.error('Error fetching chapters:', error);
        this.errorMessage = 'Failed to load chapters. Please refresh the page.';
      }
    });
  }

  getFilteredSubject(classId: number): void {
    this.loadingSubjects = true;
    this.subjects = [];
    
    this.subjectService.getSubjectsByClass(classId).subscribe({
      next: (data) => {
        this.subjects = data;
        this.loadingSubjects = false;
      },
      error: (error) => {
        console.error('Error fetching filtered subjects:', error);
        this.loadingSubjects = false;
        this.subjects = [];
        this.errorMessage = 'Failed to load subjects for the selected class.';
      }
    });
  }

  getFilteredChapter(subjectId: number, classId?: number): void {
    this.loadingChapters = true;
    this.chapters = [];
    
    this.chapterService.filterChapters(subjectId, classId).subscribe({
      next: (data) => {
        this.chapters = data;
        this.loadingChapters = false;
      },
      error: (error) => {
        console.error('Error fetching filtered chapters:', error);
        this.loadingChapters = false;
        this.chapters = [];
        this.errorMessage = 'Failed to load chapters for the selected subject.';
      }
    });
  }

  onSubjectChange(value: string): void {
    console.log('Subject changed:', value);
    
    // Clear chapter selection when subject changes
    this.questionForm.patchValue({ chapter: '' });
    
    if (value === 'all' || value === '') {
      this.getAllChapters();
    } else {
      this.getFilteredChapter(parseInt(value));
    }
  }

  onClassChange(value: string): void {
    console.log('Class changed:', value);
    
    // Clear subject and chapter selections when class changes
    this.questionForm.patchValue({ subject: '', chapter: '' });
    
    if (value === 'all' || value === '') {
      this.getAllSubjects();
    } else {
      this.getFilteredSubject(parseInt(value));
    }
  }

  // Added: Error handling method
  private handleError(error: HttpErrorResponse): void {
    console.error('HTTP Error:', error);
    
    if (error.status === 409) {
      this.errorMessage = error.error?.message || 'A question with similar content already exists.';
    } else if (error.status === 400) {
      if (error.error && error.error.fieldErrors) {
        this.errorMessage = error.error.fieldErrors
          .map((fieldError: any) => fieldError.message)
          .join(', ');
      } else {
        this.errorMessage = error.error?.message || 'Invalid data provided. Please check your input.';
      }
    } else if (error.status === 404) {
      this.errorMessage = 'Chapter not found. Please refresh the page and try again.';
    } else if (error.status === 0) {
      this.errorMessage = 'Unable to connect to server. Please check your internet connection.';
    } else {
      this.errorMessage = error.error?.message || 'An unexpected error occurred. Please try again.';
    }
  }

  // Added: Helper methods
  private markFormGroupTouched(): void {
    Object.keys(this.questionForm.controls).forEach(key => {
      this.questionForm.get(key)?.markAsTouched();
    });
  }

  private clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

  clearError(): void {
    this.errorMessage = '';
  }

  clearSuccess(): void {
    this.successMessage = '';
  }
}