import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-view-class',
  templateUrl: './view-class.component.html',
  styleUrls: ['./view-class.component.scss']
})
export class ViewClassComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  classes = [
    { id: 1, name: 'IX' },
    { id: 2, name: 'X' },
    { id: 3, name: 'XI' }
    // Add more classes as needed
  ];

  editClass(classItem: any) {
    console.log('Editing class:', classItem);
    // Add your logic to edit the class here
  }

  deleteClass(classItem: any) {
    console.log('Deleting class:', classItem);
    // Add your logic to delete the class here
  }

}
