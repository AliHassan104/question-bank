import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ClassEntity } from 'app/models/class-entities.model';
import { ClassEntityService } from 'app/services/class-entity.service';

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

  constructor(private fb: FormBuilder, private classEntityService: ClassEntityService) {
    this.classEntityForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(1)]]
    });
  }

  ngOnInit(): void {
    if (this.editingClass) {
      this.classEntityForm.patchValue(this.editingClass);
    }
  }

  ngOnChanges(): void {
    if (this.editingClass) {
      this.classEntityForm.patchValue(this.editingClass);
    } else {
      this.classEntityForm.reset();
    }
  }

  onSubmit() {
    if (this.classEntityForm.valid) {
      const newClassEntity: ClassEntity = {
        name: this.classEntityForm.value.name,
      };

      if (this.editingClass) {
        this.updateClassEntity(this.editingClass.id, newClassEntity);
      } else {
        this.createClassEntity(newClassEntity);
      }
    }
  }

  createClassEntity(classEntity: ClassEntity) {
    this.classEntityService.createClassEntity(classEntity).subscribe(
      () => {
        this.classUpdated.emit(); // Notify parent to refresh the list
        this.classEntityForm.reset(); // Reset form to add new class
      },
      error => {
        console.error('Error creating class:', error);
      }
    );
  }

  updateClassEntity(id: number, classEntity: ClassEntity) {
    this.classEntityService.updateClassEntity(id, classEntity).subscribe(
      () => {
        this.classUpdated.emit(); // Notify parent to refresh the list
        this.resetEditing.emit();  // Notify parent to stop editing mode
        this.classEntityForm.reset(); // Reset form to add mode
      },
      error => {
        console.error('Error updating class:', error);
      }
    );
  }
}
