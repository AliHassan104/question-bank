import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ClassEntity } from 'app/models/class-entities.model';
import { Subject } from 'app/models/subject.model';
import { ClassEntityService } from 'app/services/class-entity.service';
import { SubjectService } from 'app/services/subject.service';

@Component({
  selector: 'app-add-subject',
  templateUrl: './add-subject.component.html',
  styleUrls: ['./add-subject.component.scss']
})
export class AddSubjectComponent implements OnInit {
  subjectForm: FormGroup;

  @Input() editingSubject: Subject | null = null;
  @Output() subjectUpdated = new EventEmitter<void>();
  @Output() resetEditing = new EventEmitter<void>();

  classes: ClassEntity[] = [];

  constructor(
    private fb: FormBuilder,
    private classEntityService: ClassEntityService,
    private subjectService: SubjectService
  ) {}

  ngOnInit(): void {
    this.getAllClasses(0, 10);

    this.subjectForm = this.fb.group({
      name: ['', Validators.required],
      class: ['', Validators.required],
    });

    // Populate form if editingSubject is set
    if (this.editingSubject) {
      this.subjectForm.patchValue({
        name: this.editingSubject.name,
        class: this.editingSubject.classEntity?.id,
      });
    }
  }

  ngOnChanges(): void {
    if (this.editingSubject) {
      this.subjectForm.patchValue({
        name: this.editingSubject.name,
        class: this.editingSubject.classEntity?.id,
      });
    } else {
      if (this.subjectForm) {
        this.subjectForm.reset(); // Ensure form is initialized
      }
    }
  }
  
  onSubmit() {
    if (this.subjectForm.valid) {
      const newSubjectEntity: Subject = {
        name: this.subjectForm.value.name,
        classEntity: {
          id: this.subjectForm.value.class,
          name: ''
        }
      };

      if (this.editingSubject) {
        this.updateSubjectEntity(this.editingSubject.id, newSubjectEntity);
      } else {
        this.createSubjectEntity(newSubjectEntity);
      }
    }
  }

  getAllClasses(page: number, size: number) {
    this.classEntityService.getAllClassEntities(page, size).subscribe(
      data => {
        this.classes = data.content;
      },
      error => {
        console.error('Error fetching classes:', error);
      }
    );
  }

  createSubjectEntity(subject: Subject) {
    this.subjectService.createSubject(subject).subscribe(
      () => {
        this.subjectUpdated.emit(); // Notify parent to refresh subject list
        this.subjectForm.reset(); // Reset form for adding
      },
      error => {
        console.error('Error creating subject:', error);
      }
    );
  }

  updateSubjectEntity(id: number, subject: Subject) {
    this.subjectService.updateSubject(id, subject).subscribe(
      () => {
        this.subjectUpdated.emit(); // Notify parent to refresh list
        this.resetEditing.emit();   // Notify parent to stop editing
        this.subjectForm.reset();   // Reset form to add mode
      },
      error => {
        console.error('Error updating subject:', error);
      }
    );
  }
}
