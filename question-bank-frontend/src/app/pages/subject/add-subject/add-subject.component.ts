import { Component, OnInit } from '@angular/core';
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

  constructor(private fb: FormBuilder, 
              private classEntityService : ClassEntityService , 
              private subjectService : SubjectService 

  ) {}

  ngOnInit(): void {

    this.getAllClasses(0,10)

    this.subjectForm = this.fb.group({
      name: ['', Validators.required],
      class: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.subjectForm.valid) {
      const newSubjectEntity: Subject = {
        name: this.subjectForm.value.name,
        classEntity : {
          id: this.subjectForm.value.class,
          name: ''
        }
      };


      this.createSubjectEntity(newSubjectEntity);

      this.subjectForm.reset();
    }
  }

  classes: ClassEntity[] = [];
  
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

createSubjectEntity( subject: Subject) {
  this.subjectService.createSubject(subject).subscribe(
    data => {
      console.log('Class created successfully:', data);
    },
    error => {
      console.error('Error creating class:', error);
    }
  );
}

}
