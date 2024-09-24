import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-chapter',
  templateUrl: './add-chapter.component.html',
  styleUrls: ['./add-chapter.component.scss']
})
export class AddChapterComponent implements OnInit {

  chapterForm: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.chapterForm = this.fb.group({
      name: ['', Validators.required],
      class: ['', Validators.required],
      subject: ['', Validators.required],
    });
  }

  onSubmit(): void {
    if (this.chapterForm.valid) {
      console.log(this.chapterForm.value);
    }
  }


}
