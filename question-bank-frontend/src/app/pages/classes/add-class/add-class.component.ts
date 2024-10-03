import { Component, OnInit } from '@angular/core';
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

  constructor(private fb: FormBuilder, private classEntityService: ClassEntityService) {
    this.classEntityForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(1)]]
    });
  }

  ngOnInit(): void {}

  onSubmit() {
    if (this.classEntityForm.valid) {
      const newClassEntity: ClassEntity = {
        name: this.classEntityForm.value.name,
      };

      this.createClassEntity(newClassEntity);

      this.classEntityForm.reset();
    }
  }

  createClassEntity( classEntity: ClassEntity) {
    this.classEntityService.createClassEntity(classEntity).subscribe(
      data => {
        console.log('Class created successfully:', data);
      },
      error => {
        console.error('Error creating class:', error);
      }
    );
  }
}
