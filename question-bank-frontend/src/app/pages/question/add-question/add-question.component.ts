import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-question',
  templateUrl: './add-question.component.html',
  styleUrls: ['./add-question.component.scss']
})
export class AddQuestionComponent implements OnInit {

  chapterForm: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.chapterForm = this.fb.group({
      name: ['', Validators.required],
      chapter: ['', Validators.required],
    });
  }

  onSubmit(): void {
    if (this.chapterForm.valid) {
      console.log(this.chapterForm.value);
      // Handle form submission here (call backend API, etc.)
    }
  }

}
