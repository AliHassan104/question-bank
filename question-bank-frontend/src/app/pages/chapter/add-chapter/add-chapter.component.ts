import { Component, OnInit, Output, EventEmitter, Input, OnChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Chapter } from 'app/models/chapter.model';
import { ClassEntity } from 'app/models/class-entities.model';
import { Subject } from 'app/models/subject.model';
import { ChapterService } from 'app/services/chapter.service';
import { ClassEntityService } from 'app/services/class-entity.service';
import { SubjectService } from 'app/services/subject.service';

@Component({
  selector: 'app-add-chapter',
  templateUrl: './add-chapter.component.html',
  styleUrls: ['./add-chapter.component.scss']
})
export class AddChapterComponent implements OnInit, OnChanges {

  classes: ClassEntity[] = [];
  subjects: Subject[] = [];
  chapterForm: FormGroup;

  @Output() chapterAdded = new EventEmitter<void>();
  @Input() selectedChapter: Chapter | null = null;

  // Error handling properties
  errorMessage: string = '';
  successMessage: string = '';
  submitted: boolean = false;
  loading: boolean = false;
  loadingSubjects: boolean = false;

  constructor(
    private fb: FormBuilder, 
    private classEntityService: ClassEntityService,
    private subjectService: SubjectService, 
    private chapterService: ChapterService
  ) { }

  ngOnInit(): void {
    this.initializeForm();
    this.getAllClasses();
    this.getAllSubjects();

    // If we are editing, fill the form with the chapter's data
    if (this.selectedChapter) {
      this.populateFormForEdit();
    }
  }

  ngOnChanges(): void {
    // If the selected chapter changes, update the form fields
    if (this.selectedChapter && this.chapterForm) {
      this.populateFormForEdit();
    } else if (this.chapterForm) {
      this.chapterForm.reset();
    }
    // Clear messages when switching between edit/add modes
    this.clearMessages();
  }

  private initializeForm(): void {
    this.chapterForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(255)]],
      class: [''],
      subject: ['', Validators.required],
    });
  }

  private populateFormForEdit(): void {
    if (this.selectedChapter) {
      this.chapterForm.patchValue({
        name: this.selectedChapter.name,
        class: this.selectedChapter.subjectInfo.classInfo?.id || this.selectedChapter.subjectInfo.classInfo?.id,
        subject: this.selectedChapter.subjectInfo.id,
      });
    }
  }

  // Getter for easy access to form controls
  get f() { 
    return this.chapterForm.controls; 
  }

  onSubmit(): void {
    this.submitted = true;
    this.clearMessages();

    if (this.chapterForm.valid) {
      this.loading = true;

      const requestData = {
        name: this.chapterForm.value.name.trim(),
        subjectId: parseInt(this.chapterForm.value.subject)
      };


      if (this.selectedChapter) {
        this.updateChapterEntity(this.selectedChapter.id, requestData);
      } else {
        this.createChapterEntity(requestData);
      }
    }
  }

  createChapterEntity(chapterRequest: any): void {
    this.chapterService.createChapter(chapterRequest).subscribe({
      next: (data) => {
        this.loading = false;
        this.submitted = false;
        this.successMessage = 'Chapter created successfully!';
        this.chapterAdded.emit();
        this.chapterForm.reset();
        
        // Clear success message after 3 seconds
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error creating chapter:', error);
        this.loading = false;
        this.handleError(error);
      }
    });
  }

  updateChapterEntity(id: number, chapterRequest: any): void {
    // Add description and isActive for update
    const updateRequest = {
      ...chapterRequest,
      description: '', // Add empty description if not provided
      isActive: true   // Keep active by default
    };

    this.chapterService.updateChapter(id, updateRequest).subscribe({
      next: (data) => {
        this.loading = false;
        this.submitted = false;
        this.successMessage = 'Chapter updated successfully!';
        this.chapterAdded.emit();
        this.chapterForm.reset();
        
        // Clear success message after 3 seconds
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error updating chapter:', error);
        this.loading = false;
        this.handleError(error);
      }
    });
  }

  private handleError(error: HttpErrorResponse): void {
    console.error('HTTP Error:', error);
    
    if (error.status === 409) {
      // Handle duplicate resource error
      this.errorMessage = error.error?.message || 'A chapter with this name already exists in the selected subject.';
    } else if (error.status === 400) {
      // Handle validation errors
      if (error.error && error.error.fieldErrors) {
        this.errorMessage = error.error.fieldErrors
          .map((fieldError: any) => fieldError.message)
          .join(', ');
      } else {
        this.errorMessage = error.error?.message || 'Invalid data provided. Please check your input.';
      }
    } else if (error.status === 404) {
      this.errorMessage = 'Subject not found. Please refresh the page and try again.';
    } else if (error.status === 0) {
      // Network error
      this.errorMessage = 'Unable to connect to server. Please check your internet connection.';
    } else {
      // Generic error
      this.errorMessage = error.error?.message || 'An unexpected error occurred. Please try again.';
    }
  }

  getAllClasses(): void {
    this.classEntityService.getAllActiveClasses().subscribe({
      next: (data) => {
        // Filter out any invalid class objects
        this.classes = data.filter(classEntity => classEntity && classEntity.name);
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
        this.subjects = data.filter(subject => subject && subject.name && subject.name.trim() !== '');
      },
      error: (error) => {
        console.error('Error fetching subjects:', error);
        this.errorMessage = 'Failed to load subjects. Please refresh the page.';
      }
    });
  }

  onClassChange(value: string): void {
    // Clear current subject selection when class changes
    this.chapterForm.patchValue({ subject: '' });
    
    if (value === 'all' || value === '' || !value) {
      this.getAllSubjects();
    } else {
      const classId = parseInt(value);
      if (!isNaN(classId)) {
        this.getFilteredSubject(classId);
      }
    }
  }

  // ✅ This method is CORRECT - it matches your controller's path variable pattern
  getFilteredSubject(classId: number): void {
    this.loadingSubjects = true;
    this.subjects = []; // Clear current subjects while loading
    
    // This calls: GET /api/subjects/class/{classId} - matches your @PathVariable controller
    this.subjectService.getSubjectsByClass(classId).subscribe({
      next: (data) => {
        
        // Filter out any invalid subject objects
        this.subjects = data.filter(subjectEntity => {
          const isValid = subjectEntity && 
                          subjectEntity.name && 
                          subjectEntity.name.trim() !== '' &&
                          subjectEntity.id;
          
          if (!isValid) {
            console.warn('Invalid filtered subject object found:', subjectEntity);
          }
          return isValid;
        });
        
        this.loadingSubjects = false;
        
        // Show message if no subjects found
        if (this.subjects.length === 0) {
          this.errorMessage = 'No subjects found for the selected class.';
          setTimeout(() => {
            this.errorMessage = '';
          }, 3000);
        }
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error fetching filtered subjects:', error);
        this.loadingSubjects = false;
        this.subjects = [];
        
        if (error.status === 404) {
          this.errorMessage = 'Class not found or has no subjects.';
        } else {
          this.errorMessage = 'Failed to load subjects for the selected class.';
        }
        
        // Clear error after 5 seconds
        setTimeout(() => {
          this.errorMessage = '';
        }, 5000);
      }
    });
  }

  private clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

  // Method to manually clear error message
  clearError(): void {
    this.errorMessage = '';
  }

  // Method to manually clear success message
  clearSuccess(): void {
    this.successMessage = '';
  }
}