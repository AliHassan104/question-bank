import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-subject',
  templateUrl: './add-subject.component.html',
  styleUrls: ['./add-subject.component.scss']
})
export class AddSubjectComponent implements OnInit {
  subjectForm: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.subjectForm = this.fb.group({
      name: ['', Validators.required],
      subject: ['', Validators.required],
    });
  }

  onSubmit(): void {
    if (this.subjectForm.valid) {
      console.log(this.subjectForm.value);
      // Handle the form submission here
    }
  }

}
