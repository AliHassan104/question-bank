import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-class',
  templateUrl: './add-class.component.html',
  styleUrls: ['./add-class.component.scss']
})
export class AddClassComponent implements OnInit {

  ngOnInit(): void {
  }

  nameForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.nameForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(1)]]
    });
  }

  onSubmit() {
    if (this.nameForm.valid) {
      console.log('Name added:', this.nameForm.value.name);
      alert('Name added: ' + this.nameForm.value.name);
      this.nameForm.reset(); // Reset the form after submission
    }
  }

}
