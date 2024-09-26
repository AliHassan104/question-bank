import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-view-subject',
  templateUrl: './view-subject.component.html',
  styleUrls: ['./view-subject.component.scss']
})
export class ViewSubjectComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  subjects = [
    { id: 1, name: 'Maths', class: 'IX' },
    { id: 2, name: 'Physics', class: 'X' },
    { id: 3, name: 'Chemistry', class: 'XI' }
    // Add more subjects as needed
  ];

  editSubject(subject: any) {
    console.log('Editing subject:', subject);
    // Add your logic to edit the subject here
  }

  deleteSubject(subject: any) {
    console.log('Deleting subject:', subject);
    // Add your logic to delete the subject here
  }


}
