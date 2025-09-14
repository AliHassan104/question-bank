import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ClassEntity } from 'app/models/class-entities.model';
import { ClassEntityService } from 'app/services/class-entity.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-add-class',
  templateUrl: './add-class.component.html',
  styleUrls: ['./add-class.component.scss']
})
export class AddClassComponent implements OnInit {

  classEntityForm: FormGroup;

  @Output() classUpdated = new EventEmitter<void>();
  @Output() resetEditing = new EventEmitter<void>();
  @Input() editingClass: ClassEntity | null = null;

  // Error handling properties
  errorMessage: string = '';
  successMessage: string = '';
  submitted: boolean = false;
  loading: boolean = false;

  constructor(private fb: FormBuilder, private classEntityService: ClassEntityService) {
    // Simple form matching backend ClassEntity structure
    this.classEntityForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(500)]] // Optional description field
    });
  }

  ngOnInit(): void {
    if (this.editingClass) {
      this.classEntityForm.patchValue({
        name: this.editingClass.name,
        description: this.editingClass.description || ''
      });
    }
  }

  ngOnChanges(): void {
    if (this.editingClass) {
      this.classEntityForm.patchValue({
        name: this.editingClass.name,
        description: this.editingClass.description || ''
      });
    } else {
      this.classEntityForm.reset();
    }
    // Clear messages when switching between edit/add modes
    this.clearMessages();
  }

  onSubmit() {
    this.submitted = true;
    this.clearMessages();

    if (this.classEntityForm.valid) {
      this.loading = true;
      
      // Create ClassEntity object matching backend expectations
      const newClassEntity: ClassEntity = {
        name: this.classEntityForm.value.name.trim(),
        description: this.classEntityForm.value.description?.trim() || null
      };

      if (this.editingClass) {
        this.updateClassEntity(this.editingClass.id, newClassEntity);
      } else {
        this.createClassEntity(newClassEntity);
      }
    } else {
      console.error('Form is invalid:', this.classEntityForm.errors);
      this.markFormGroupTouched();
    }
  }

  createClassEntity(classEntity: ClassEntity) {
    
    this.classEntityService.createClassEntity(classEntity).subscribe({
      next: (response) => {
        this.loading = false;
        this.submitted = false;
        this.successMessage = 'Class created successfully!';
        this.classUpdated.emit();
        this.classEntityForm.reset();
        
        // Clear success message after 3 seconds
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error creating class:', error);
        this.loading = false;
        this.handleError(error);
      }
    });
  }

  updateClassEntity(id: number, classEntity: ClassEntity) {
    
    this.classEntityService.updateClassEntity(id, classEntity).subscribe({
      next: (response) => {
        this.loading = false;
        this.submitted = false;
        this.successMessage = 'Class updated successfully!';
        this.classUpdated.emit();
        this.resetEditing.emit();
        this.classEntityForm.reset();
        
        // Clear success message after 3 seconds
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error updating class:', error);
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
        this.errorMessage = 'A class with this name already exists.';
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

  // Helper method to mark all form fields as touched (for validation display)
  private markFormGroupTouched() {
    Object.keys(this.classEntityForm.controls).forEach(key => {
      this.classEntityForm.get(key)?.markAsTouched();
    });
  }

  // Method to manually clear error message
  clearError() {
    this.errorMessage = '';
  }

  // Method to manually clear success message
  clearSuccess() {
    this.successMessage = '';
  }

  // Getter methods for template validation
  get name() { return this.classEntityForm.get('name'); }
  get description() { return this.classEntityForm.get('description'); }
}