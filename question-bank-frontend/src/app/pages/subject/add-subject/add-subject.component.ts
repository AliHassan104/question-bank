import { Component, OnInit, Input, Output, EventEmitter, OnChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ClassEntity } from 'app/models/class-entities.model';
import { Subject, CreateSubjectRequest, UpdateSubjectRequest } from 'app/models/subject.model';
import { ClassEntityService } from 'app/services/class-entity.service';
import { SubjectService } from 'app/services/subject.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-add-subject',
  templateUrl: './add-subject.component.html',
  styleUrls: ['./add-subject.component.scss']
})
export class AddSubjectComponent implements OnInit, OnChanges {
  subjectForm: FormGroup;

  @Input() editingSubject: Subject | null = null;
  @Output() subjectUpdated = new EventEmitter<void>();
  @Output() resetEditing = new EventEmitter<void>();

  classes: ClassEntity[] = [];
  
  // Error handling properties
  errorMessage: string = '';
  successMessage: string = '';
  submitted: boolean = false;
  loading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private classEntityService: ClassEntityService,
    private subjectService: SubjectService
  ) { }

  ngOnInit(): void {
    this.getAllClasses(0, 10);

    this.subjectForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      class: ['', Validators.required],
    });

    if (this.editingSubject) {
      this.subjectForm.patchValue({
        name: this.editingSubject.name,
        description: this.editingSubject.description,
        class: this.editingSubject.classInfo?.id,
      });
    }
  }

  ngOnChanges(): void {
    if (this.editingSubject) {
      this.subjectForm.patchValue({
        name: this.editingSubject.name,
        description: this.editingSubject.description,
        class: this.editingSubject.classInfo?.id,
      });
    } else {
      if (this.subjectForm) {
        this.subjectForm.reset();
      }
    }
    // Clear messages when switching between edit/add modes
    this.clearMessages();
  }

  // Getter for easy access to form controls
  get f() { 
    return this.subjectForm.controls; 
  }

  onSubmit() {
    this.submitted = true;
    this.clearMessages();

    if (this.subjectForm.valid) {
      this.loading = true;
      
      if (this.editingSubject) {
        const updateRequest: UpdateSubjectRequest = {
          name: this.subjectForm.value.name,
          description: this.subjectForm.value.description,
          classId: parseInt(this.subjectForm.value.class),
          isActive: this.editingSubject.isActive
        };
        this.updateSubjectEntity(this.editingSubject.id, updateRequest);
      } else {
        const createRequest: CreateSubjectRequest = {
          name: this.subjectForm.value.name,
          description: this.subjectForm.value.description,
          classId: parseInt(this.subjectForm.value.class)
        };
        this.createSubjectEntity(createRequest);
      }
    }
  }

  getAllClasses(page: number, size: number) {
    this.classEntityService.getAllActiveClasses().subscribe({
      next: (data) => {
        this.classes = data;
      },
      error: (error) => {
        console.error('Error fetching classes:', error);
        this.errorMessage = 'Failed to load classes. Please try again.';
      }
    });
  }

  createSubjectEntity(subject: CreateSubjectRequest) {
    this.subjectService.createSubject(subject).subscribe({
      next: (response) => {
        this.loading = false;
        this.submitted = false;
        this.successMessage = 'Subject created successfully!';
        this.subjectUpdated.emit();
        this.subjectForm.reset();
        
        // Clear success message after 3 seconds
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (error: HttpErrorResponse) => {
        this.loading = false;
        this.handleError(error);
      }
    });
  }

  updateSubjectEntity(id: number, subject: UpdateSubjectRequest) {
    this.subjectService.updateSubject(id, subject).subscribe({
      next: (response) => {
        this.loading = false;
        this.submitted = false;
        this.successMessage = 'Subject updated successfully!';
        this.subjectUpdated.emit();
        this.resetEditing.emit();
        this.subjectForm.reset();
        
        // Clear success message after 3 seconds
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (error: HttpErrorResponse) => {
        this.loading = false;
        this.handleError(error);
      }
    });
  }

  private handleError(error: HttpErrorResponse) {
    console.error('HTTP Error:', error);
    
    if (error.status === 409) {
      // Handle duplicate resource error
      if (error.error && error.error.message) {
        this.errorMessage = error.error.message;
      } else {
        this.errorMessage = 'A subject with this name already exists in the selected class.';
      }
    } else if (error.status === 400) {
      // Handle validation errors
      if (error.error && error.error.fieldErrors) {
        this.errorMessage = error.error.fieldErrors
          .map((fieldError: any) => fieldError.message)
          .join(', ');
      } else if (error.error && error.error.message) {
        this.errorMessage = error.error.message;
      } else {
        this.errorMessage = 'Invalid data provided. Please check your input.';
      }
    } else if (error.status === 0) {
      // Network error
      this.errorMessage = 'Unable to connect to server. Please check your internet connection.';
    } else {
      // Generic error
      this.errorMessage = error.error?.message || 'An unexpected error occurred. Please try again.';
    }
  }

  private clearMessages() {
    this.errorMessage = '';
    this.successMessage = '';
  }

  // Method to manually clear error message
  clearError() {
    this.errorMessage = '';
  }

  // Method to manually clear success message
  clearSuccess() {
    this.successMessage = '';
  }
}